package net.warsmash.uberserver;

import net.warsmash.nio.util.DisconnectListener;

public interface GamingNetworkClientToServerListener extends DisconnectListener {
	void handshake(String gameId, int version);

	void createAccount(String username, char[] passwordHash);

	void login(String username, char[] passwordHash);

	void joinChannel(long sessionToken, String channelName);

	void chatMessage(long sessionToken, String text);

	void emoteMessage(long sessionToken, String text);

	class Protocol {
		public static final int HANDSHAKE = 1;
		public static final int CREATE_ACCOUNT = 2;
		public static final int LOGIN = 3;
		public static final int JOIN_CHANNEL = 4;
		public static final int CHAT_MESSAGE = 5;
		public static final int EMOTE_MESSAGE = 6;
	}
}
