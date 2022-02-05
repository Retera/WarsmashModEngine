package net.warsmash.uberserver;

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
	}
}
