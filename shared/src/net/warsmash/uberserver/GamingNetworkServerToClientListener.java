package net.warsmash.uberserver;

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

	void joinedGame(String gameName);

	void joinGameFailed(JoinGameFailureReason joinGameFailureReason);

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
		public void joinedGame(final String gameName) {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.joinedGame(gameName);
			}
		}

		@Override
		public void joinGameFailed(final JoinGameFailureReason joinGameFailureReason) {
			for (final GamingNetworkServerToClientListener listener : this.listeners) {
				listener.joinGameFailed(joinGameFailureReason);
			}
		}

	}
}
