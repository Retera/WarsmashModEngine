package net.warsmash.uberserver;

import net.warsmash.nio.util.DisconnectListener;

public interface GamingNetworkClientToServerListener extends DisconnectListener {
	void handshake(String gameId, int version);

	void createAccount(String username, char[] passwordHash);

	void login(String username, char[] passwordHash);

	void joinChannel(long sessionToken, String channelName);

	void chatMessage(long sessionToken, String text);

	void emoteMessage(long sessionToken, String text);

	void queryGamesList(long sessionToken);

	void queryGameInfo(long sessionToken, String gameName);

	void joinGame(long sessionToken, String gameName);

	void createGame(final long sessionToken, final String gameName, final String mapName, final int totalSlots,
			final LobbyGameSpeed gameSpeed, final long gameCreationTimeMillis, HostedGameVisibility visibility);

	class Protocol {
		public static final int HANDSHAKE = 1;
		public static final int CREATE_ACCOUNT = 2;
		public static final int LOGIN = 3;
		public static final int JOIN_CHANNEL = 4;
		public static final int CHAT_MESSAGE = 5;
		public static final int EMOTE_MESSAGE = 6;
		public static final int QUERY_GAMES_LIST = 7;
		public static final int QUERY_GAME_INFO = 8;
		public static final int JOIN_GAME = 9;
		public static final int CREATE_GAME = 10;
	}
}
