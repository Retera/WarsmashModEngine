package com.etheller.warsmash.networking.uberserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.etheller.warsmash.networking.uberserver.users.PasswordAuthentication;
import com.etheller.warsmash.networking.uberserver.users.User;
import com.etheller.warsmash.networking.uberserver.users.UserManager;

import net.warsmash.uberserver.AccountCreationFailureReason;
import net.warsmash.uberserver.GamingNetworkServerToClientListener;
import net.warsmash.uberserver.HandshakeDeniedReason;
import net.warsmash.uberserver.JoinGameFailureReason;
import net.warsmash.uberserver.LoginFailureReason;

public class GamingNetworkServerBusinessLogicImpl {
	private final Set<AcceptedGameListKey> acceptedGames;
	private final UserManager userManager;
	private final String welcomeMessage;
	private final Map<Integer, SessionImpl> userIdToCurrentSession = new HashMap<>();
	private final Map<Long, SessionImpl> tokenToSession;
	private final Map<String, ChatChannel> nameLowerCaseToChannel = new HashMap<>();
	private final Map<String, HostedGame> nameLowerCaseToGame = new HashMap<>();
	private final Random random;

	public GamingNetworkServerBusinessLogicImpl(final Set<AcceptedGameListKey> acceptedGames,
			final UserManager userManager, final String welcomeMessage) {
		this.acceptedGames = acceptedGames;
		this.userManager = userManager;
		this.welcomeMessage = welcomeMessage;
		this.tokenToSession = new HashMap<>();
		this.random = new Random();
	}

	public void handshake(final String gameId, final int version,
			final GamingNetworkServerToClientListener connectionContext) {
		if (this.acceptedGames.contains(new AcceptedGameListKey(gameId, version))) {
			connectionContext.handshakeAccepted();
		}
		else {
			connectionContext.handshakeDenied(HandshakeDeniedReason.BAD_GAME_VERSION);
		}
	}

	public void createAccount(final String username, final char[] passwordHash,
			final GamingNetworkServerToClientListener connectionContext) {
		final User user = this.userManager.createUser(username, passwordHash);
		if (user == null) {
			connectionContext.accountCreationFailed(AccountCreationFailureReason.USERNAME_ALREADY_EXISTS);
		}
		else {
			connectionContext.accountCreationOk();
		}
	}

	public void login(final String username, final char[] passwordHash,
			final GamingNetworkServerToClientListener connectionContext) {
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
			}
			else {
				connectionContext.loginFailed(LoginFailureReason.INVALID_CREDENTIALS);
			}
		}
		else {
			connectionContext.loginFailed(LoginFailureReason.UNKNOWN_USER);
		}
	}

	private void killSession(final SessionImpl currentSession) {
		removeSessionFromCurrentChannel(currentSession);
		this.tokenToSession.remove(currentSession.getToken());
		this.userIdToCurrentSession.remove(currentSession.getUser().getId());
	}

	public void joinChannel(final long sessionToken, final String channelName,
			final GamingNetworkServerToClientListener connectionContext) {
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
			connectionContext.joinedChannel(channelName);
		}
		else {
			connectionContext.badSession();
		}
	}

	public void joinGame(final long sessionToken, final String gameName,
			final GamingNetworkServerToClientListener connectionContext) {
		final SessionImpl session = getSession(sessionToken, connectionContext);
		if (session != null) {
			removeSessionFromCurrentChannel(session);

			final String gameKey = gameName.toLowerCase(Locale.US);
			final HostedGame game = this.nameLowerCaseToGame.get(gameName);
			if (game == null) {
				connectionContext.joinGameFailed(JoinGameFailureReason.NO_SUCH_GAME);
			}
			game.addUser(session);
			session.currentGameName = gameKey;
			connectionContext.joinedGame(gameName);
		}
		else {
			connectionContext.badSession();
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
		}
	}

	public void chatMessage(final long sessionToken, final String text,
			final GamingNetworkServerToClientListener connectionContext) {
		final SessionImpl session = getSession(sessionToken, connectionContext);
		if (session != null) {
			final String channelKey = session.currentChatChannel.toLowerCase(Locale.US);
			final ChatChannel chatChannel = this.nameLowerCaseToChannel.get(channelKey);
			if (chatChannel != null) {
				chatChannel.sendMessage(session.getUser().getUsername(), text);
			}
		}
		else {
			connectionContext.badSession();
		}
	}

	public void emoteMessage(final long sessionToken, final String text,
			final GamingNetworkServerToClientListener connectionContext) {
		final SessionImpl session = getSession(sessionToken, connectionContext);
		if (session != null) {
			final String channelKey = session.currentChatChannel.toLowerCase(Locale.US);
			final ChatChannel chatChannel = this.nameLowerCaseToChannel.get(channelKey);
			if (chatChannel != null) {
				chatChannel.sendEmote(session.getUser().getUsername(), text);
			}
		}
		else {
			connectionContext.badSession();
		}
	}

	public void queryGamesList(final long sessionToken, final GamingNetworkServerToClientListener connectionContext) {
		final SessionImpl session = getSession(sessionToken, connectionContext);
		if (session != null) {
			connectionContext.beginGamesList();
			for (final Map.Entry<String, HostedGame> nameAndGame : this.nameLowerCaseToGame.entrySet()) {
				final HostedGame game = nameAndGame.getValue();
				connectionContext.gamesListItem(nameAndGame.getKey(), game.getUsedSlots(), game.getTotalSlots());
			}
			connectionContext.endGamesList();
		}
		else {
			connectionContext.badSession();
		}
	}

	public void queryGameInfo(final long sessionToken, final GamingNetworkServerToClientWriter connectionContext) {
		final SessionImpl session = getSession(sessionToken, connectionContext);
		if (session != null) {
			// TODO
		}
		else {
			connectionContext.badSession();
		}
	}

	private SessionImpl getSession(final long token,
			final GamingNetworkServerToClientListener mostRecentConnectionContext) {
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
		private String currentChatChannel;
		private String currentGameName;
		private GamingNetworkServerToClientListener mostRecentConnectionContext;

		public SessionImpl(final User user, final long timestamp, final long secretKey,
				final GamingNetworkServerToClientListener connectionContext) {
			this.user = user;
			this.timestamp = timestamp;
			this.secretKey = secretKey;
			this.lastActiveTime = timestamp;
			this.mostRecentConnectionContext = connectionContext;
		}

		public void notifyUsed(final GamingNetworkServerToClientListener mostRecentConnectionContext) {
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

	private static final class HostedGame {
		private final String gameName;
		private final List<SessionImpl> userSessions = new ArrayList<>();
		private final int totalSlots;

		public HostedGame(final String gameName, final int totalSlots) {
			this.gameName = gameName;
			this.totalSlots = totalSlots;
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

		public int getUsedSlots() {
			return this.userSessions.size();
		}

		public int getTotalSlots() {
			return this.totalSlots;
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

}
