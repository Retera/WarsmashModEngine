package com.etheller.warsmash.networking.uberserver;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.etheller.warsmash.networking.WarsmashServer;
import com.etheller.warsmash.networking.uberserver.users.PasswordAuthentication;
import com.etheller.warsmash.networking.uberserver.users.User;
import com.etheller.warsmash.networking.uberserver.users.UserManager;

import net.warsmash.map.NetMapDownloader;
import net.warsmash.uberserver.*;

public class GamingNetworkServerBusinessLogicImpl {
	private final Set<AcceptedGameListKey> acceptedGames;
	private final UserManager userManager;
	private final String welcomeMessage;
	private final GamingNetworkServerTracker tracker;
	private final Map<Integer, SessionImpl> userIdToCurrentSession = new HashMap<>();
	private final Map<Long, SessionImpl> tokenToSession;
	private final Map<String, ChatChannel> nameLowerCaseToChannel = new HashMap<>();
	private final Map<String, HostedGame> nameLowerCaseToGame = new HashMap<>();
	private final Random random;

	public GamingNetworkServerBusinessLogicImpl(final Set<AcceptedGameListKey> acceptedGames,
			final UserManager userManager, final String welcomeMessage, GamingNetworkServerTracker tracker) {
		this.acceptedGames = acceptedGames;
		this.userManager = userManager;
		this.welcomeMessage = welcomeMessage;
		this.tracker = tracker;
		this.tokenToSession = new HashMap<>();
		this.random = new Random();
	}

	public void disconnected(final GamingNetworkServerToClientWriter writer) {
		// TODO this is not efficient, and may make DDOS hit us badly
		SessionImpl sessionToKill = null;
		for (final SessionImpl session : this.tokenToSession.values()) {
			if (session.mostRecentConnectionContext == writer) {
				sessionToKill = session;
			}
		}
		if (sessionToKill != null) {
			killSession(sessionToKill);
			tracker.disconnectedSession(writer.getAddressString(), sessionToKill.getUser());
		}
	}

	public void handshake(final String gameId, final int version,
			final GamingNetworkClientConnectionContext connectionContext) {
		AcceptedGameListKey acceptedGameListKey = new AcceptedGameListKey(gameId, version);
		if (this.acceptedGames.contains(acceptedGameListKey)) {
			connectionContext.handshakeAccepted();
			tracker.handshakeAccepted(connectionContext.getAddressString(), acceptedGameListKey);
		}
		else {
			connectionContext.handshakeDenied(HandshakeDeniedReason.BAD_GAME_VERSION);
			tracker.handshakeDenied(connectionContext.getAddressString(), HandshakeDeniedReason.BAD_GAME_VERSION);
		}
	}

	public void createAccount(final String username, final char[] passwordHash,
			final GamingNetworkClientConnectionContext connectionContext) {
		final User user = this.userManager.createUser(username, passwordHash);
		if (user == null) {
			connectionContext.accountCreationFailed(AccountCreationFailureReason.USERNAME_ALREADY_EXISTS);
			tracker.accountCreationFailed(connectionContext.getAddressString(), username);
		}
		else {
			connectionContext.accountCreationOk();
			tracker.accountCreatedOk(connectionContext.getAddressString(), username);
		}
	}

	public void login(final String username, final char[] passwordHash,
			final GamingNetworkClientConnectionContext connectionContext) {
		final User user = this.userManager.getUserByName(username);
		if (user != null) {
			if (PasswordAuthentication.authenticate(passwordHash, user.getPasswordHash())) {
				final SessionImpl currentSession = this.userIdToCurrentSession.get(user.getId());
				if (currentSession != null) {
					killSession(currentSession);
				}
				final long timestamp = System.currentTimeMillis();
				final SessionImpl session = new SessionImpl(user, timestamp, this.random.nextLong(), connectionContext);
				this.tokenToSession.put(session.getToken(), session);
				this.userIdToCurrentSession.put(user.getId(), session);
				connectionContext.loginOk(session.getToken(), GamingNetworkServerBusinessLogicImpl.this.welcomeMessage);
				tracker.loginOk(connectionContext.getAddressString(), username);
			}
			else {
				connectionContext.loginFailed(LoginFailureReason.INVALID_CREDENTIALS);
				tracker.loginFailed(connectionContext.getAddressString(), LoginFailureReason.INVALID_CREDENTIALS);
			}
		}
		else {
			connectionContext.loginFailed(LoginFailureReason.UNKNOWN_USER);
			tracker.loginFailed(connectionContext.getAddressString(), LoginFailureReason.UNKNOWN_USER);
		}
	}

	private void killSession(final SessionImpl currentSession) {
		removeSessionFromCurrentChannel(currentSession);
		removeSessionFromCurrentGame(currentSession);
		this.tokenToSession.remove(currentSession.getToken());
		this.userIdToCurrentSession.remove(currentSession.getUser().getId());
		try {
			String addressString = currentSession.mostRecentConnectionContext.getAddressString();
			currentSession.mostRecentConnectionContext.disconnected();
			tracker.killedSession(addressString, currentSession.getUser());
		}
		catch (final Exception exc) {
			System.err.println("Exception while killing session for user: " + exc);
			exc.printStackTrace();
		}
	}

	public void joinChannel(final long sessionToken, final String channelName,
			final GamingNetworkClientConnectionContext connectionContext) {
		final SessionImpl session = getSession(sessionToken, connectionContext);
		if (session != null) {
			removeSessionFromCurrentChannel(session);

			final String channelKey = channelName.toLowerCase(Locale.US);
			ChatChannel chatChannel = this.nameLowerCaseToChannel.get(channelKey);
			if (chatChannel == null) {
				chatChannel = new ChatChannel(channelName);
				this.nameLowerCaseToChannel.put(channelKey, chatChannel);
			}
			chatChannel.addUser(session);
			session.currentChatChannel = channelKey;
			session.lastActiveChatChannel = chatChannel.channelName;
			connectionContext.joinedChannel(channelName);
			tracker.joinedChannel(connectionContext.getAddressString(), session.getUser(), channelName);
		}
		else {
			connectionContext.badSession();
			tracker.badSession(connectionContext.getAddressString());
		}
	}

	public void joinGame(final long sessionToken, final String gameName,
			final GamingNetworkClientConnectionContext connectionContext) {
		final SessionImpl session = getSession(sessionToken, connectionContext);
		if (session != null) {
			if (session.currentGameName != null) {
				connectionContext.joinGameFailed(JoinGameFailureReason.SESSION_ERROR);
				tracker.joinGameFailed(connectionContext.getAddressString(), session.getUser(), gameName, JoinGameFailureReason.SESSION_ERROR);
			}
			else {
				removeSessionFromCurrentChannel(session);

				final String gameKey = gameName.toLowerCase(Locale.US);
				final HostedGame game = this.nameLowerCaseToGame.get(gameKey);
				if (game == null) {
					connectionContext.joinGameFailed(JoinGameFailureReason.NO_SUCH_GAME);
					tracker.joinGameFailed(connectionContext.getAddressString(), session.getUser(), gameName, JoinGameFailureReason.NO_SUCH_GAME);
				}
				else {
					if (game.getUsedSlots() >= game.totalSlots) {
						connectionContext.joinGameFailed(JoinGameFailureReason.GAME_FULL);
						tracker.joinGameFailed(connectionContext.getAddressString(), session.getUser(), gameName, JoinGameFailureReason.GAME_FULL);
					}
					else {
						game.addUser(session);
						session.currentGameName = gameKey;
						connectionContext.joinedGame(gameName, game.mapName, game.mapChecksum);
						game.sendServerMessage(session.getUser().getUsername(), ChannelServerMessageType.JOIN_GAME);
						game.resendLobby(connectionContext);
						tracker.joinedGame(connectionContext.getAddressString(), session.getUser(), gameName);
					}
				}
			}
		}
		else {
			connectionContext.badSession();
			tracker.badSession(connectionContext.getAddressString());
		}
	}

	public void leaveGame(final long sessionToken, final GamingNetworkServerToClientWriter connectionContext) {
		final SessionImpl session = getSession(sessionToken, connectionContext);
		if (session != null) {
			removeSessionFromCurrentGame(session);
			sendSessionToDefaultChannel(sessionToken, connectionContext, session);
			tracker.leftGame(connectionContext.getAddressString(), session.getUser());
		}
		else {
			connectionContext.badSession();
			tracker.badSession(connectionContext.getAddressString());
		}
	}

	public void uploadMapData(final long sessionToken, final int sequenceNumber, final ByteBuffer data,
			final GamingNetworkServerToClientWriter connectionContext) {
		final SessionImpl session = getSession(sessionToken, connectionContext);
		if (session != null) {
			if (session.currentGameName == null) {
				connectionContext.serverErrorMessage(ServerErrorMessageType.ERROR_HANDLING_REQUEST);
				tracker.updateMapFailedNoGame(connectionContext.getAddressString(), session.getUser());
			}
			else {
				final HostedGame hostedGame = this.nameLowerCaseToGame.get(session.currentGameName);
				hostedGame.writeMap(sessionToken, sequenceNumber, data);
				tracker.writeMap(connectionContext.getAddressString(), session.getUser());
			}
		}
		else {
			connectionContext.badSession();
			tracker.badSession(connectionContext.getAddressString());
		}
	}

	public void mapDone(final long sessionToken, final int sequenceNumber,
			final GamingNetworkServerToClientWriter connectionContext) {
		final SessionImpl session = getSession(sessionToken, connectionContext);
		if (session != null) {
			if (session.currentGameName == null) {
				connectionContext.serverErrorMessage(ServerErrorMessageType.ERROR_HANDLING_REQUEST);
				tracker.mapDoneFailedNoGame(connectionContext.getAddressString(), session.getUser());
			}
			else {
				final HostedGame hostedGame = this.nameLowerCaseToGame.get(session.currentGameName);
				if (!hostedGame.mapDone(sessionToken, sequenceNumber)) {
					closeGame(session.currentGameName, hostedGame);
					connectionContext.serverErrorMessage(ServerErrorMessageType.UPLOAD_MAP_FAILED);
					tracker.uploadMapFailed(connectionContext.getAddressString(), session.getUser());
				}
				else {
					hostedGame.sendMapToAwaitingUsers();
					tracker.uploadMapSucceeded(connectionContext.getAddressString(), session.getUser());
				}
			}
		}
		else {
			connectionContext.badSession();
			tracker.badSession(connectionContext.getAddressString());
		}
	}

	public void requestMap(final long sessionToken, final GamingNetworkServerToClientWriter connectionContext) {
		final SessionImpl session = getSession(sessionToken, connectionContext);
		if (session != null) {
			if (session.currentGameName == null) {
				connectionContext.serverErrorMessage(ServerErrorMessageType.ERROR_HANDLING_REQUEST);
				tracker.requestMapFailedNoGame(connectionContext.getAddressString(), session.getUser());
			}
			else {
				final HostedGame hostedGame = this.nameLowerCaseToGame.get(session.currentGameName);
				if (hostedGame.isMapFullyLoaded()) {
					// easy case: send map
					System.err.println(session.getUser().getUsername() + " - sending map");
					hostedGame.sendMap(connectionContext);
					hostedGame.resendLobby(connectionContext);
					tracker.sentMap(connectionContext.getAddressString(), session.getUser());
				}
				else {
					System.err.println(
							session.getUser().getUsername() + " - waiting for map because server wasn't ready");
					hostedGame.addPlayerAwaitingMap(session);
					tracker.subscribedForMap(connectionContext.getAddressString(), session.getUser());
				}
			}
		}
		else {
			connectionContext.badSession();
			tracker.badSession(connectionContext.getAddressString());
		}
	}

	private void sendSessionToDefaultChannel(final SessionImpl session) {
		sendSessionToDefaultChannel(session.getToken(), session.mostRecentConnectionContext, session);
	}

	private void sendSessionToDefaultChannel(final long sessionToken,
			final GamingNetworkServerToClientListener connectionContext, final SessionImpl session) {
		if (session.lastActiveChatChannel != null) {
			joinChannel(sessionToken, session.lastActiveChatChannel, connectionContext);
		}
		else {
			// TODO maybe some other message here to let client know to return to
			// the welcome screen?? Should we just let them handle both of these cases?
			joinChannel(sessionToken, "Default Channel", connectionContext);
		}
	}

	public void createGame(final long sessionToken, final String gameName, final String mapName, final int totalSlots,
			final LobbyGameSpeed gameSpeed, final HostedGameVisibility visibility, final long mapChecksum,
			final GamingNetworkServerToClientWriter connectionContext) {
		final SessionImpl session = getSession(sessionToken, connectionContext);
		if (session != null) {

			final String gameKey = gameName.toLowerCase(Locale.US);
			HostedGame game = this.nameLowerCaseToGame.get(gameKey);
			if (game != null) {
				connectionContext.gameCreationFailed(GameCreationFailureReason.GAME_NAME_ALREADY_USED);
				tracker.gameCreationFailed(connectionContext.getAddressString(), session.getUser(), GameCreationFailureReason.GAME_NAME_ALREADY_USED);
			}
			else {
				removeSessionFromCurrentChannel(session);
				game = new HostedGame(session.getUser(), gameName, mapName, totalSlots, gameSpeed, visibility,
						mapChecksum);
				this.nameLowerCaseToGame.put(gameKey, game);
				connectionContext.gameCreationOk();
				game.addUser(session);
				session.currentGameName = gameKey;
				connectionContext.joinedGame(gameName, game.mapName, game.mapChecksum);
				game.sendServerMessage(session.getUser().getUsername(), ChannelServerMessageType.JOIN_GAME);
				game.resendLobby(connectionContext);
				tracker.createdGame(connectionContext.getAddressString(), session.getUser(), gameName, mapName, totalSlots, gameSpeed, visibility, mapChecksum);
			}
		}
		else {
			connectionContext.badSession();
			tracker.badSession(connectionContext.getAddressString());
		}
	}

	public void gameLobbySetPlayerSlot(final long sessionToken, final int slot, final LobbyPlayerType lobbyPlayerType,
			final GamingNetworkServerToClientWriter connectionContext) {
		final SessionImpl session = getSession(sessionToken, connectionContext);
		if (session != null) {
			if (session.currentGameName != null) {
				final String channelKey = session.currentGameName.toLowerCase(Locale.US);
				final HostedGame game = this.nameLowerCaseToGame.get(channelKey);
				if (game != null) {
					if (game.hostUser == session.getUser()) {
						game.setPlayerSlotType(slot, lobbyPlayerType);
						tracker.gameLobbySetPlayerSlotType(connectionContext.getAddressString(), session.getUser(), slot, lobbyPlayerType);
					}
					else {
						connectionContext.serverErrorMessage(ServerErrorMessageType.ERROR_HANDLING_REQUEST);
						tracker.gameLobbySetPlayerSlotTypeFailed(connectionContext.getAddressString(), session.getUser(), slot, lobbyPlayerType, LobbyActionFailureReason.NOT_HOST);
					}
				}
				else {
					connectionContext.serverErrorMessage(ServerErrorMessageType.ERROR_HANDLING_REQUEST);
					tracker.gameLobbySetPlayerSlotTypeFailed(connectionContext.getAddressString(), session.getUser(), slot, lobbyPlayerType, LobbyActionFailureReason.NO_GAME);
				}
			}
			else {
				connectionContext.serverErrorMessage(ServerErrorMessageType.ERROR_HANDLING_REQUEST);
				tracker.gameLobbySetPlayerSlotTypeFailed(connectionContext.getAddressString(), session.getUser(), slot, lobbyPlayerType, LobbyActionFailureReason.NO_SESSION_GAME);
			}
		}
		else {
			connectionContext.badSession();
			tracker.badSession(connectionContext.getAddressString());
		}
	}

	public void gameLobbySetPlayerRace(final long sessionToken, final int slot, final int raceItemIndex,
			final GamingNetworkServerToClientWriter connectionContext) {
		final SessionImpl session = getSession(sessionToken, connectionContext);
		if (session != null) {
			if (session.currentGameName != null) {
				final String channelKey = session.currentGameName.toLowerCase(Locale.US);
				final HostedGame game = this.nameLowerCaseToGame.get(channelKey);
				if (game != null) {
					if (game.canSetRace(session, slot)) {
						game.setPlayerRace(slot, raceItemIndex);
						tracker.gameLobbySetPlayerRace(connectionContext.getAddressString(), session.getUser(), slot, raceItemIndex);
					}
					else {
						connectionContext.serverErrorMessage(ServerErrorMessageType.ERROR_HANDLING_REQUEST);
						tracker.gameLobbySetPlayerRaceFailed(connectionContext.getAddressString(), session.getUser(), slot, raceItemIndex, LobbyActionFailureReason.NOT_HOST_OR_SLOT);
					}
				}
				else {
					connectionContext.serverErrorMessage(ServerErrorMessageType.ERROR_HANDLING_REQUEST);
					tracker.gameLobbySetPlayerRaceFailed(connectionContext.getAddressString(), session.getUser(), slot, raceItemIndex, LobbyActionFailureReason.NO_GAME);
				}
			}
			else {
				connectionContext.serverErrorMessage(ServerErrorMessageType.ERROR_HANDLING_REQUEST);
				tracker.gameLobbySetPlayerRaceFailed(connectionContext.getAddressString(), session.getUser(), slot, raceItemIndex, LobbyActionFailureReason.NO_SESSION_GAME);
			}
		}
		else {
			connectionContext.badSession();
			tracker.badSession(connectionContext.getAddressString());
		}
	}

	public void gameLobbyStartGame(final long sessionToken, final GamingNetworkServerToClientWriter connectionContext) {
		final SessionImpl session = getSession(sessionToken, connectionContext);
		if (session != null) {
			if (session.currentGameName != null) {
				final String channelKey = session.currentGameName.toLowerCase(Locale.US);
				final HostedGame game = this.nameLowerCaseToGame.get(channelKey);
				if (game != null) {
					if (game.getHostUser() == session.getUser()) {
						game.onStartGame();
						tracker.gameLobbyStartGame(connectionContext.getAddressString(), session.getUser(), channelKey);
					}
					else {
						connectionContext.serverErrorMessage(ServerErrorMessageType.ERROR_HANDLING_REQUEST);
						tracker.gameLobbyStartGameFailed(connectionContext.getAddressString(), session.getUser(), channelKey, LobbyActionFailureReason.NOT_HOST);
					}
				}
				else {
					connectionContext.serverErrorMessage(ServerErrorMessageType.ERROR_HANDLING_REQUEST);
					tracker.gameLobbyStartGameFailed(connectionContext.getAddressString(), session.getUser(), channelKey, LobbyActionFailureReason.NO_GAME);
				}
			}
			else {
				connectionContext.serverErrorMessage(ServerErrorMessageType.ERROR_HANDLING_REQUEST);
				tracker.gameLobbyStartGameFailed(connectionContext.getAddressString(), session.getUser(), null, LobbyActionFailureReason.NO_SESSION_GAME);
			}
		}
		else {
			connectionContext.badSession();
			tracker.badSession(connectionContext.getAddressString());
		}
	}

	private void removeSessionFromCurrentChannel(final SessionImpl session) {
		final String previousChatChannel = session.currentChatChannel;
		if (previousChatChannel != null) {
			final String previousChannelKey = previousChatChannel.toLowerCase(Locale.US);
			final ChatChannel previousChannel = this.nameLowerCaseToChannel.get(previousChannelKey);
			previousChannel.removeUser(session);
			if (previousChannel.isEmpty()) {
				this.nameLowerCaseToChannel.remove(previousChannelKey);
			}
			session.currentChatChannel = null;
		}
	}

	private void removeSessionFromCurrentGame(final SessionImpl session) {
		final String previousGameName = session.currentGameName;
		if (previousGameName != null) {
			final String previousGameKey = previousGameName.toLowerCase(Locale.US);
			final HostedGame previousGame = this.nameLowerCaseToGame.get(previousGameKey);
			previousGame.removeUser(session);
			session.currentGameName = null;
			if (previousGame.isEmpty()) {
				this.nameLowerCaseToGame.remove(previousGameKey);
			}
			else if (session.getUser() == previousGame.getHostUser()) {
				// host leaves the game, but it is not empty.
				closeGame(previousGameKey, previousGame);

			}
			else {
				previousGame.sendServerMessage(session.getUser().getUsername(), ChannelServerMessageType.LEAVE_GAME);
			}
		}
	}

	private void closeGame(final String previousGameKey, final HostedGame previousGame) {
		// 1.) notify clients that they were booted from game
		for (final SessionImpl nonHostUserSession : previousGame.userSessionSlots) {
			if (nonHostUserSession != null) {
				nonHostUserSession.currentGameName = null;
				sendSessionToDefaultChannel(nonHostUserSession);
			}
		}
		// 2.) close down the game
		this.nameLowerCaseToGame.remove(previousGameKey);
		previousGame.onCloseGame();
	}

	public void chatMessage(final long sessionToken, final String text,
			final GamingNetworkClientConnectionContext connectionContext) {
		final SessionImpl session = getSession(sessionToken, connectionContext);
		if (session != null) {
			if (session.currentChatChannel != null) {
				final String channelKey = session.currentChatChannel.toLowerCase(Locale.US);
				final ChatChannel chatChannel = this.nameLowerCaseToChannel.get(channelKey);
				if (chatChannel != null) {
					chatChannel.sendMessage(session.getUser().getUsername(), text);
					tracker.channelChatMessage(connectionContext.getAddressString(), session.getUser(), channelKey, text);
				}
			}
			else if (session.currentGameName != null) {
				final String channelKey = session.currentGameName.toLowerCase(Locale.US);
				final HostedGame chatChannel = this.nameLowerCaseToGame.get(channelKey);
				if (chatChannel != null) {
					chatChannel.sendMessage(session.getUser().getUsername(), text);
					tracker.gameLobbyChatMessage(connectionContext.getAddressString(), session.getUser(), channelKey, text);
				}
			}
			else {
				connectionContext.serverErrorMessage(ServerErrorMessageType.ERROR_HANDLING_REQUEST);
				tracker.chatMessageFailed(connectionContext.getAddressString(), session.getUser(), text);
			}
		}
		else {
			connectionContext.badSession();
			tracker.badSession(connectionContext.getAddressString());
		}
	}

	public void emoteMessage(final long sessionToken, final String text,
			final GamingNetworkClientConnectionContext connectionContext) {
		final SessionImpl session = getSession(sessionToken, connectionContext);
		if (session != null) {
			if (session.currentChatChannel != null) {
				final String channelKey = session.currentChatChannel.toLowerCase(Locale.US);
				final ChatChannel chatChannel = this.nameLowerCaseToChannel.get(channelKey);
				if (chatChannel != null) {
					chatChannel.sendEmote(session.getUser().getUsername(), text);
					tracker.channelEmoteMessage(connectionContext.getAddressString(), session.getUser(), channelKey, text);
				}
			}
			else if (session.currentGameName != null) {
				final String channelKey = session.currentGameName.toLowerCase(Locale.US);
				final HostedGame chatChannel = this.nameLowerCaseToGame.get(channelKey);
				if (chatChannel != null) {
					chatChannel.sendEmote(session.getUser().getUsername(), text);
					tracker.gameLobbyEmoteMessage(connectionContext.getAddressString(), session.getUser(), channelKey, text);
				}
			}
			else {
				connectionContext.serverErrorMessage(ServerErrorMessageType.ERROR_HANDLING_REQUEST);
				tracker.emoteMessageFailed(connectionContext.getAddressString(), session.getUser(), text);
			}
		}
		else {
			connectionContext.badSession();
			tracker.badSession(connectionContext.getAddressString());
		}
	}

	public void queryGamesList(final long sessionToken, final GamingNetworkClientConnectionContext connectionContext) {
		final SessionImpl session = getSession(sessionToken, connectionContext);
		if (session != null) {
			// TODO this code paradigm is stupid and gives the client an incentive to modify
			// their client to request this as frequently as possible. That is stupid, and
			// the client should instead send us some form of subscribe message after which
			// we would send them a throttled feed. However, the setup for the throttled
			// feed felt like it would be a waste of time so in the interest of development
			// time, for now I am doing a basic request/reply that allows the client to ask
			// us for the games list whenever they want.
			connectionContext.beginGamesList();
			for (final Map.Entry<String, HostedGame> nameAndGame : this.nameLowerCaseToGame.entrySet()) {
				final HostedGame game = nameAndGame.getValue();
				if (game.visibility == HostedGameVisibility.PUBLIC) {
					connectionContext.gamesListItem(game.getGameName(), game.getUsedSlots(), game.getTotalSlots());
				}
			}
			connectionContext.endGamesList();
			tracker.queriedGamesList(connectionContext.getAddressString(), session.getUser());
		}
		else {
			connectionContext.badSession();
			tracker.badSession(connectionContext.getAddressString());
		}
	}

	public void queryGameInfo(final long sessionToken, final GamingNetworkServerToClientWriter connectionContext) {
		final SessionImpl session = getSession(sessionToken, connectionContext);
		if (session != null) {
			// TODO
		}
		else {
			connectionContext.badSession();
			tracker.badSession(connectionContext.getAddressString());
		}
	}

	private SessionImpl getSession(final long token,
			final GamingNetworkClientConnectionContext mostRecentConnectionContext) {
		final SessionImpl session = this.tokenToSession.get(token);
		if (session != null) {
			if (session.getLastActiveTime() < (System.currentTimeMillis() - (60 * 60 * 1000))) {
				killSession(session);
				return null;
			}
			else {
				session.notifyUsed(mostRecentConnectionContext);
				return session;
			}
		}
		return null;
	}

	private static final class SessionImpl {
		private final User user;
		private final long timestamp;
		private final long secretKey;
		private long lastActiveTime;
		private String lastActiveChatChannel;
		private String currentChatChannel;
		private String currentGameName;
		private GamingNetworkClientConnectionContext mostRecentConnectionContext;

		public SessionImpl(final User user, final long timestamp, final long secretKey,
				final GamingNetworkClientConnectionContext connectionContext) {
			this.user = user;
			this.timestamp = timestamp;
			this.secretKey = secretKey;
			this.lastActiveTime = timestamp;
			this.mostRecentConnectionContext = connectionContext;
		}

		public void notifyUsed(final GamingNetworkClientConnectionContext mostRecentConnectionContext) {
			this.lastActiveTime = System.currentTimeMillis();
			this.mostRecentConnectionContext = mostRecentConnectionContext;
		}

		public long getLastActiveTime() {
			return this.lastActiveTime;
		}

		public User getUser() {
			return this.user;
		}

		public long getTimestamp() {
			return this.timestamp;
		}

		public long getToken() {
			final int nameCode = this.user.getUsername().hashCode();
			return (this.timestamp & 0xFFFFFFFF)
					| (((this.secretKey << 32) + ((long) nameCode << 32)) & 0xFFFFFFFF00000000L);
		}
	}

	private static final class ChatChannel {
		private final String channelName;
		private final List<SessionImpl> userSessions = new ArrayList<>();

		public ChatChannel(final String channelName) {
			this.channelName = channelName;
		}

		public void removeUser(final SessionImpl session) {
			this.userSessions.remove(session);
		}

		public void addUser(final SessionImpl session) {
			this.userSessions.add(session);
		}

		public boolean isEmpty() {
			return this.userSessions.isEmpty();
		}

		public void sendMessage(final String sourceUserName, final String message) {
			for (final SessionImpl session : this.userSessions) {
				try {
					session.mostRecentConnectionContext.channelMessage(sourceUserName, message);
				}
				catch (final Exception exc) {
					exc.printStackTrace();
				}
			}
		}

		public void sendEmote(final String sourceUserName, final String message) {
			for (final SessionImpl session : this.userSessions) {
				try {
					session.mostRecentConnectionContext.channelEmote(sourceUserName, message);
				}
				catch (final Exception exc) {
					exc.printStackTrace();
				}
			}
		}
	}

	private static final class HostedGamePlayerData {
		private LobbyPlayerType type = LobbyPlayerType.OPEN;
		private int raceItemIndex = -1;
	}

	private static final class HostedGame {
		private final User hostUser;
		private final String gameName;
		private final String mapName;
		private final int totalSlots;
		private final LobbyGameSpeed gameSpeed;
		private final HostedGameVisibility visibility;
		private final long mapChecksum;
		private final SessionImpl[] userSessionSlots;
		private final HostedGamePlayerData[] userSessionSlotsGameData;
		private final List<SessionImpl> userSessionsAwaitingMap = new ArrayList<>();
		private NetMapDownloader mapDownloader;
		private File mapFile;
		private boolean mapFullyLoaded = false;
		private WarsmashServer warsmashGameServer;

		public HostedGame(final User hostUser, final String gameName, final String mapName, final int totalSlots,
				final LobbyGameSpeed gameSpeed, final HostedGameVisibility visibility, final long mapChecksum) {
			this.hostUser = hostUser;
			this.gameName = gameName;
			this.mapName = mapName;
			this.totalSlots = totalSlots;
			this.gameSpeed = gameSpeed;
			this.visibility = visibility;
			this.mapChecksum = mapChecksum;
			this.userSessionSlots = new SessionImpl[totalSlots];
			this.userSessionSlotsGameData = new HostedGamePlayerData[totalSlots];
			for (int i = 0; i < totalSlots; i++) {
				this.userSessionSlotsGameData[i] = new HostedGamePlayerData();
			}
		}

		public boolean mapDone(final long sessionToken, final int sequenceNumber) {
			final long finishedChecksum = this.mapDownloader.finish(sequenceNumber);
			this.mapFullyLoaded = (finishedChecksum == this.mapChecksum) && this.mapDownloader.isSequenceNumberingOK();
			return this.mapFullyLoaded;
		}

		public void sendMap(final GamingNetworkServerToClientListener connectionContext) {
			int sequenceNumber = 0;
			connectionContext.beginSendMap();
			try (FileChannel readerChannel = FileChannel.open(this.mapFile.toPath(), StandardOpenOption.READ)) {
				final ByteBuffer readBuffer = ByteBuffer.allocate(1300).clear();
				while ((readerChannel.read(readBuffer)) != -1) {
					readBuffer.flip();
					connectionContext.sendMapData(sequenceNumber++, readBuffer);
					readBuffer.clear();
				}
			}
			catch (final IOException e) {
				throw new IllegalStateException(e);
			}
			connectionContext.endSendMap(sequenceNumber);
		}

		public boolean isMapFullyLoaded() {
			return this.mapFullyLoaded;
		}

		public void writeMap(final long sessionToken, final int sequenceNumber, final ByteBuffer data) {
			if (this.mapDownloader == null) {
				final int id = this.hostUser.getId();
				final String nameToStoreMap = id + "_" + sessionToken + "_";
				try {
					this.mapFile = File.createTempFile(nameToStoreMap, ".dat");
				}
				catch (final IOException e) {
					throw new IllegalStateException(e);
				}
				this.mapDownloader = new NetMapDownloader(this.mapFile);
			}
			this.mapDownloader.receive(sequenceNumber, data);
		}

		public User getHostUser() {
			return this.hostUser;
		}

		public void removeUser(final SessionImpl session) {
			int returnIndex = -1;
			for (int i = 0; i < this.userSessionSlots.length; i++) {
				if (this.userSessionSlots[i] == session) {
					returnIndex = i;
				}
			}
			if (returnIndex != -1) {
				this.userSessionSlots[returnIndex] = null;
				this.userSessionSlotsGameData[returnIndex].type = LobbyPlayerType.OPEN;

				for (int i = 0; i < this.userSessionSlots.length; i++) {
					if (this.userSessionSlots[i] != null) {
						this.userSessionSlots[i].mostRecentConnectionContext.gameLobbySlotSetPlayerType(returnIndex,
								LobbyPlayerType.OPEN);
					}
				}
			}
			this.userSessionsAwaitingMap.remove(session);
		}

		public int addUser(final SessionImpl session) {
			int returnIndex = -1;
			for (int i = 0; i < this.userSessionSlots.length; i++) {
				if (this.userSessionSlots[i] == null) {
					returnIndex = i;
					break;
				}
			}
			if (returnIndex != -1) {
				this.userSessionSlots[returnIndex] = session;
				final HostedGamePlayerData hostedGamePlayerData = this.userSessionSlotsGameData[returnIndex];
				hostedGamePlayerData.type = LobbyPlayerType.USER;

				for (int i = 0; i < this.userSessionSlots.length; i++) {
					if ((this.userSessionSlots[i] != null) && (this.userSessionSlots[i] != session)) {
						// upon adding user X, we do not send to himself. At the moment, that is handled
						// elsewhere...
						this.userSessionSlots[i].mostRecentConnectionContext.gameLobbySlotSetPlayerType(returnIndex,
								LobbyPlayerType.USER);
						this.userSessionSlots[i].mostRecentConnectionContext.gameLobbySlotSetPlayer(returnIndex,
								session.getUser().getUsername());
					}
				}
			}
			return returnIndex;
		}

		public void addPlayerAwaitingMap(final SessionImpl session) {
			this.userSessionsAwaitingMap.add(session);
		}

		public void resendLobby(final GamingNetworkServerToClientListener connectionContext) {
			for (int i = 0; i < this.userSessionSlotsGameData.length; i++) {
				connectionContext.gameLobbySlotSetPlayerType(i, this.userSessionSlotsGameData[i].type);
				if (this.userSessionSlots[i] != null) {
					connectionContext.gameLobbySlotSetPlayer(i, this.userSessionSlots[i].getUser().getUsername());
				}
				if (this.userSessionSlotsGameData[i].raceItemIndex != -1) {
					connectionContext.gameLobbySlotSetPlayerRace(i, this.userSessionSlotsGameData[i].raceItemIndex);
				}
			}
		}

		public boolean isEmpty() {
			return getUsedSlots() == 0;
		}

		public String getGameName() {
			return this.gameName;
		}

		public int getUsedSlots() {
			int usedSlots = 0;
			for (int i = 0; i < this.userSessionSlots.length; i++) {
				if (this.userSessionSlots[i] != null) {
					usedSlots++;
				}
			}
			return usedSlots;
		}

		public int getTotalSlots() {
			return this.totalSlots;
		}

		public void sendMessage(final String sourceUserName, final String message) {
			for (int i = 0; i < this.userSessionSlots.length; i++) {
				final SessionImpl session = this.userSessionSlots[i];
				if (session != null) {
					try {
						session.mostRecentConnectionContext.channelMessage(sourceUserName, message);
					}
					catch (final Exception exc) {
						exc.printStackTrace();
					}
				}
			}
		}

		public void sendEmote(final String sourceUserName, final String message) {
			for (int i = 0; i < this.userSessionSlots.length; i++) {
				final SessionImpl session = this.userSessionSlots[i];
				if (session != null) {
					try {
						session.mostRecentConnectionContext.channelEmote(sourceUserName, message);
					}
					catch (final Exception exc) {
						exc.printStackTrace();
					}
				}
			}
		}

		public void sendServerMessage(final String sourceUserName, final ChannelServerMessageType message) {
			for (int i = 0; i < this.userSessionSlots.length; i++) {
				final SessionImpl session = this.userSessionSlots[i];
				if (session != null) {
					try {
						session.mostRecentConnectionContext.channelServerMessage(sourceUserName, message);
					}
					catch (final Exception exc) {
						exc.printStackTrace();
					}
				}
			}
		}

		public void setPlayerSlotType(final int slot, final LobbyPlayerType lobbyPlayerType) {
			if (lobbyPlayerType != LobbyPlayerType.USER) {
				this.userSessionSlotsGameData[slot].type = lobbyPlayerType;
				this.userSessionSlots[slot] = null;
				for (int i = 0; i < this.userSessionSlots.length; i++) {
					if (this.userSessionSlots[i] != null) {
						this.userSessionSlots[i].mostRecentConnectionContext.gameLobbySlotSetPlayerType(slot,
								lobbyPlayerType);
					}
				}
			}
		}

		public boolean canSetRace(final SessionImpl session, final int slot) {
			return ((session.getUser() == this.hostUser)
					&& (this.userSessionSlotsGameData[slot].type != LobbyPlayerType.USER))
					|| (session == this.userSessionSlots[slot]);
		}

		public void setPlayerRace(final int slot, final int raceItemIndex) {
			this.userSessionSlotsGameData[slot].raceItemIndex = raceItemIndex;
			for (int i = 0; i < this.userSessionSlots.length; i++) {
				if (this.userSessionSlots[i] != null) {
					this.userSessionSlots[i].mostRecentConnectionContext.gameLobbySlotSetPlayerRace(slot,
							raceItemIndex);
				}
			}
		}

		public void setPlayerSlot(final SessionImpl session, final int slot) {
			int returnIndex = -1;
			for (int i = 0; i < this.userSessionSlots.length; i++) {
				if (this.userSessionSlots[i] == session) {
					returnIndex = i;
				}
			}
			if ((returnIndex != -1) && (slot != returnIndex)) {
				if ((slot >= 0) && (slot < this.userSessionSlots.length)) {
					if ((this.userSessionSlots[slot] == null)
							&& (this.userSessionSlotsGameData[slot].type == LobbyPlayerType.OPEN)) {

						this.userSessionSlots[returnIndex] = null;
						this.userSessionSlotsGameData[returnIndex].type = LobbyPlayerType.OPEN;

						this.userSessionSlots[slot] = session;
						this.userSessionSlotsGameData[slot].type = LobbyPlayerType.USER;

						for (int i = 0; i < this.userSessionSlots.length; i++) {
							if (this.userSessionSlots[i] != null) {
								this.userSessionSlots[i].mostRecentConnectionContext
										.gameLobbySlotSetPlayerType(returnIndex, LobbyPlayerType.OPEN);
								this.userSessionSlots[i].mostRecentConnectionContext.gameLobbySlotSetPlayerType(slot,
										LobbyPlayerType.USER);
								this.userSessionSlots[i].mostRecentConnectionContext.gameLobbySlotSetPlayer(slot,
										session.getUser().getUsername());
							}
						}
					}
				}

			}
		}

		public void sendMapToAwaitingUsers() {
			for (final SessionImpl session : this.userSessionsAwaitingMap) {
				sendMap(session.mostRecentConnectionContext);
				resendLobby(session.mostRecentConnectionContext);
			}
			this.userSessionsAwaitingMap.clear();
		}

		public void onCloseGame() {
			if (this.mapFile != null) {
				this.mapFile.delete();
			}
		}

		public void onStartGame() {
			final Map<Long, Integer> sessionTokenToSlot = new HashMap<>();
			for (int i = 0; i < this.userSessionSlotsGameData.length; i++) {
				if (this.userSessionSlotsGameData[i].type == LobbyPlayerType.USER) {
					sessionTokenToSlot.put(this.userSessionSlots[i].getToken(), i);
				}
			}

			try {
				this.warsmashGameServer = new WarsmashServer(0, sessionTokenToSlot);
			}
			catch (final IOException e) {
				e.printStackTrace();
			}
			if (this.warsmashGameServer != null) {
				this.warsmashGameServer.startThread();
				final InetSocketAddress localAddress = this.warsmashGameServer.getLocalAddress();
				if (localAddress != null) {
					InetAddress localHost;
					try {
						localHost = getLocalHost();
					}
					catch (final Exception e) {
						e.printStackTrace();
						return;
					}
					if (localHost == null) {
						System.err.println(
								"Unable to find local host address in GamingNetworkServerBusinessLogicImpl!!!");
						return;
					}
					System.out.println("Game address: " + localHost);
					final byte[] bytes = localHost.getAddress();
					final short port = (short) localAddress.getPort();
					for (int i = 0; i < this.userSessionSlots.length; i++) {
						final SessionImpl session = this.userSessionSlots[i];
						if (session != null) {
							try {
								session.mostRecentConnectionContext.gameLobbyStartGame(bytes, port, i);
							}
							catch (final Exception exc) {
								exc.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	private static InetAddress getLocalHost() throws SocketException {
		final Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
		InetAddress somePublicAddress = null;
		for (; n.hasMoreElements();) {
			final NetworkInterface e = n.nextElement();

			if (!e.isLoopback() && e.isUp()) {
				final Enumeration<InetAddress> a = e.getInetAddresses();
				for (; a.hasMoreElements();) {
					final InetAddress addr = a.nextElement();
					if (addr instanceof Inet4Address) {
						somePublicAddress = addr;
					}
				}
			}
		}
		return somePublicAddress;
	}

}
