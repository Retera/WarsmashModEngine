package net.warsmash.uberserver;

import java.nio.ByteBuffer;

public interface GamingNetworkConnection extends GamingNetworkClientToServerListener {
	void addListener(GamingNetworkServerToClientListener listener);

	void userRequestDisconnect();

	boolean userRequestConnect();

	String getGatewayString();

	GamingNetworkConnection NONE = new GamingNetworkConnection() {

		@Override
		public void disconnected() {
		}

		@Override
		public void uploadMapData(final long sessionToken, final int sequenceNumber, final ByteBuffer data) {
		}

		@Override
		public void requestMap(final long sessionToken) {
		}

		@Override
		public void queryGamesList(final long sessionToken) {
		}

		@Override
		public void queryGameInfo(final long sessionToken, final String gameName) {
		}

		@Override
		public void mapDone(final long sessionToken, final int sequenceNumber) {
		}

		@Override
		public void login(final String username, final char[] passwordHash) {
		}

		@Override
		public void leaveGame(final long sessionToken) {
		}

		@Override
		public void joinGame(final long sessionToken, final String gameName) {
		}

		@Override
		public void joinChannel(final long sessionToken, final String channelName) {
		}

		@Override
		public void handshake(final String gameId, final int version) {
		}

		@Override
		public void gameLobbyStartGame(final long sessionToken) {
		}

		@Override
		public void gameLobbySetPlayerSlot(final long sessionToken, final int slot,
				final LobbyPlayerType lobbyPlayerType) {
		}

		@Override
		public void gameLobbySetPlayerRace(final long sessionToken, final int slot, final int raceItemIndex) {
		}

		@Override
		public void emoteMessage(final long sessionToken, final String text) {
		}

		@Override
		public void createGame(final long sessionToken, final String gameName, final String mapName,
				final int totalSlots, final LobbyGameSpeed gameSpeed, final HostedGameVisibility visibility,
				final long mapChecksum) {
		}

		@Override
		public void createAccount(final String username, final char[] passwordHash) {
		}

		@Override
		public void chatMessage(final long sessionToken, final String text) {
		}

		@Override
		public void userRequestDisconnect() {
		}

		@Override
		public boolean userRequestConnect() {
			return false;
		}

		@Override
		public String getGatewayString() {
			return null;
		}

		@Override
		public void addListener(final GamingNetworkServerToClientListener listener) {
		}
	};
}
