package net.warsmash.uberserver;

import net.warsmash.nio.util.DisconnectListener;

public interface GamingNetworkClientToServerListener extends DisconnectListener {
	void handshake(String gameId, int version);

	void createAccount(String username, String password);

	void login(String username, String password);

	void joinChannel(String channelName);

	void chatMessage(String text);

	void emoteMessage(String text);

	class Protocol {
		public static final int HANDSHAKE = 1;
		public static final int CREATE_ACCOUNT = 2;
		public static final int LOGIN = 3;
		public static final int JOIN_CHANNEL = 4;
		public static final int CHAT_MESSAGE = 5;
		public static final int EMOTE_MESSAGE = 6;
	}
}
