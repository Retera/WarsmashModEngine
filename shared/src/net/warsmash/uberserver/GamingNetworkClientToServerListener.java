package net.warsmash.uberserver;

import java.nio.ByteBuffer;

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
			final LobbyGameSpeed gameSpeed, HostedGameVisibility visibility, long mapChecksum);

	void leaveGame(long sessionToken);

	void uploadMapData(long sessionToken, int sequenceNumber, ByteBuffer data);

	void mapDone(long sessionToken, int sequenceNumber);

	void requestMap(long sessionToken);

	void gameLobbySetPlayerSlot(long sessionToken, int slot, LobbyPlayerType lobbyPlayerType);

	void gameLobbySetPlayerRace(long sessionToken, int slot, int raceItemIndex);

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
		public static final int LEAVE_GAME = 11;
		public static final int UPLOAD_MAP_DATA = 12;
		public static final int UPLOAD_MAP_DATA_DONE = 13;
		public static final int REQUEST_MAP = 14;
		public static final int GAME_LOBBY_SET_PLAYER_SLOT = 15;
		public static final int GAME_LOBBY_SET_PLAYER_RACE = 16;
	}
}
