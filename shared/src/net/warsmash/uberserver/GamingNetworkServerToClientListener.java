package net.warsmash.uberserver;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import net.warsmash.nio.util.DisconnectListener;

public interface GamingNetworkServerToClientListener extends DisconnectListener {
	void handshakeAccepted();

	void handshakeDenied(HandshakeDeniedReason reason);

	void accountCreationOk();

	void accountCreationFailed(AccountCreationFailureReason reason);

	void loginOk(long sessionToken, String welcomeMessage);

	void loginFailed(LoginFailureReason loginFailureReason);

	void joinedChannel(String channelName);

	void badSession();

	void channelMessage(String userName, String message);

	void channelEmote(String userName, String message);

	void beginGamesList();

	void gamesListItem(String gameName, int openSlots, int totalSlots);

	void endGamesList();

	void joinedGame(String gameName, String mapName, long mapChecksum);

	void joinGameFailed(JoinGameFailureReason joinGameFailureReason);

	void gameCreationOk();

	void gameCreationFailed(GameCreationFailureReason reason);

	void channelServerMessage(String userName, ChannelServerMessageType messageType);

	void beginSendMap();

	void sendMapData(int sequenceNumber, ByteBuffer data);

	void endSendMap(int sequenceNumber);

	void serverErrorMessage(ServerErrorMessageType messageType);

	void gameLobbySlotSetPlayer(int slot, String userName);

	void gameLobbySlotSetPlayerType(int slot, LobbyPlayerType playerType);

	void gameLobbySlotSetPlayerRace(int slot, int raceItemIndex);

	class Protocol {
		public static final int HANDSHAKE_ACCEPTED = 1;
		public static final int HANDSHAKE_DENIED = 2;
		public static final int ACCOUNT_CREATION_OK = 3;
		public static final int ACCOUNT_CREATION_FAILED = 4;
		public static final int LOGIN_OK = 5;
		public static final int LOGIN_FAILED = 6;
		public static final int JOINED_CHANNEL = 7;
		public static final int BAD_SESSION = 8;
		public static final int CHANNEL_MESSAGE = 9;
		public static final int CHANNEL_EMOTE = 10;
		public static final int BEGIN_GAMES_LIST = 11;
		public static final int GAMES_LIST_ITEM = 12;
		public static final int END_GAMES_LIST = 13;
		public static final int JOINED_GAME = 14;
		public static final int JOIN_GAME_FAILED = 15;
		public static final int GAME_CREATION_OK = 16;
		public static final int GAME_CREATION_FAILED = 17;
		public static final int CHANNEL_SERVER_MESSAGE = 18;
		public static final int BEGIN_SEND_MAP = 19;
		public static final int SEND_MAP_DATA = 20;
		public static final int END_SEND_MAP = 21;
		public static final int SERVER_ERROR_MESSAGE = 22;
		public static final int GAME_LOBBY_SET_PLAYER = 23;
		public static final int GAME_LOBBY_SET_PLAYER_TYPE = 24;
		public static final int GAME_LOBBY_SET_PLAYER_RACE = 25;
	}

	public static final class GamingNetworkServerToClientNotifier implements GamingNetworkServerToClientListener {
		private final List<GamingNetworkServerToClientListener> listeners = new ArrayList<>();

		public void addSubscriber(final GamingNetworkServerToClientListener listener) {
			this.listeners.add(listener);
		}

		@Override
		public void disconnected() {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.disconnected();
			}
		}

		@Override
		public void handshakeAccepted() {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.handshakeAccepted();
			}
		}

		@Override
		public void handshakeDenied(final HandshakeDeniedReason reason) {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.handshakeDenied(reason);
			}
		}

		@Override
		public void accountCreationOk() {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.accountCreationOk();
			}
		}

		@Override
		public void accountCreationFailed(final AccountCreationFailureReason reason) {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.accountCreationFailed(reason);
			}
		}

		@Override
		public void loginOk(final long sessionToken, final String welcomeMessage) {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.loginOk(sessionToken, welcomeMessage);
			}
		}

		@Override
		public void loginFailed(final LoginFailureReason loginFailureReason) {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.loginFailed(loginFailureReason);
			}
		}

		@Override
		public void joinedChannel(final String channelName) {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.joinedChannel(channelName);
			}
		}

		@Override
		public void badSession() {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.badSession();
			}
		}

		@Override
		public void channelMessage(final String userName, final String message) {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.channelMessage(userName, message);
			}
		}

		@Override
		public void channelEmote(final String userName, final String message) {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.channelEmote(userName, message);
			}
		}

		@Override
		public void beginGamesList() {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.beginGamesList();
			}
		}

		@Override
		public void gamesListItem(final String gameName, final int openSlots, final int totalSlots) {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.gamesListItem(gameName, openSlots, totalSlots);
			}
		}

		@Override
		public void endGamesList() {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.endGamesList();
			}
		}

		@Override
		public void joinedGame(final String gameName, String mapName, long mapChecksum) {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.joinedGame(gameName, mapName, mapChecksum);
			}
		}

		@Override
		public void joinGameFailed(final JoinGameFailureReason joinGameFailureReason) {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.joinGameFailed(joinGameFailureReason);
			}
		}

		@Override
		public void gameCreationOk() {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.gameCreationOk();
			}
		}

		@Override
		public void gameCreationFailed(GameCreationFailureReason reason) {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.gameCreationFailed(reason);
			}
		}

		@Override
		public void channelServerMessage(String userName, ChannelServerMessageType messageType) {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.channelServerMessage(userName, messageType);
			}
		}

		@Override
		public void beginSendMap() {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.beginSendMap();
			}
		}

		@Override
		public void sendMapData(int sequenceNumber, ByteBuffer data) {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.sendMapData(sequenceNumber, data);
			}
		}

		@Override
		public void endSendMap(int sequenceNumber) {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.endSendMap(sequenceNumber);
			}
		}

		@Override
		public void serverErrorMessage(ServerErrorMessageType messageType) {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.serverErrorMessage(messageType);
			}
		}

		@Override
		public void gameLobbySlotSetPlayer(int slot, String userName) {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.gameLobbySlotSetPlayer(slot, userName);
			}
		}

		@Override
		public void gameLobbySlotSetPlayerType(int slot, LobbyPlayerType playerType) {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.gameLobbySlotSetPlayerType(slot, playerType);
			}
		}

		@Override
		public void gameLobbySlotSetPlayerRace(int slot, int raceItemIndex) {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.gameLobbySlotSetPlayerRace(slot, raceItemIndex);
			}
		}
	}

	public static abstract class GamingNetworkServerToClientListenerAdapter
			implements GamingNetworkServerToClientListener {

		@Override
		public void disconnected() {
		}

		@Override
		public void handshakeAccepted() {
		}

		@Override
		public void handshakeDenied(HandshakeDeniedReason reason) {
		}

		@Override
		public void accountCreationOk() {
		}

		@Override
		public void accountCreationFailed(AccountCreationFailureReason reason) {
		}

		@Override
		public void loginOk(long sessionToken, String welcomeMessage) {
		}

		@Override
		public void loginFailed(LoginFailureReason loginFailureReason) {
		}

		@Override
		public void joinedChannel(String channelName) {
		}

		@Override
		public void badSession() {
		}

		@Override
		public void channelMessage(String userName, String message) {
		}

		@Override
		public void channelEmote(String userName, String message) {
		}

		@Override
		public void beginGamesList() {
		}

		@Override
		public void gamesListItem(String gameName, int openSlots, int totalSlots) {
		}

		@Override
		public void endGamesList() {
		}

		@Override
		public void joinedGame(String gameName, String mapName, long mapChecksum) {
		}

		@Override
		public void joinGameFailed(JoinGameFailureReason joinGameFailureReason) {
		}

		@Override
		public void gameCreationOk() {
		}

		@Override
		public void gameCreationFailed(GameCreationFailureReason reason) {
		}

		@Override
		public void channelServerMessage(String userName, ChannelServerMessageType messageType) {
		}

		@Override
		public void beginSendMap() {
		}

		@Override
		public void sendMapData(int sequenceNumber, ByteBuffer data) {
		}

		@Override
		public void endSendMap(int sequenceNumber) {
		}

		@Override
		public void serverErrorMessage(ServerErrorMessageType messageType) {
		}

		@Override
		public void gameLobbySlotSetPlayer(int slot, String userName) {
		}

		@Override
		public void gameLobbySlotSetPlayerType(int slot, LobbyPlayerType playerType) {
		}

		@Override
		public void gameLobbySlotSetPlayerRace(int slot, int raceItemIndex) {
		}
	}
}
