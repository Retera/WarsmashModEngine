package net.warsmash.uberserver;

public interface GamingNetworkServerToClientListener {
	void handshakeAccepted();

	void handshakeDenied(HandshakeDeniedReason reason);

	void accountCreationOk();

	void accountCreationFailed(AccountCreationFailureReason reason);

	void loginOk(String welcomeMessage);

	void joinedChannel(String channelName);

	class Protocol {
		public static final int HANDSHAKE_ACCEPTED = 1;
		public static final int HANDSHAKE_DENIED = 2;
		public static final int ACCOUNT_CREATION_OK = 3;
		public static final int ACCOUNT_CREATION_FAILED = 4;
		public static final int LOGIN_OK = 5;
		public static final int JOINED_CHANNEL = 6;
	}
}
