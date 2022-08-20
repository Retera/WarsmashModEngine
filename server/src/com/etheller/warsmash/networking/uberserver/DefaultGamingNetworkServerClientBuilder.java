package com.etheller.warsmash.networking.uberserver;

import java.nio.ByteBuffer;

import net.warsmash.nio.channels.WritableOutput;
import net.warsmash.uberserver.GamingNetworkClientToServerListener;
import net.warsmash.uberserver.HostedGameVisibility;
import net.warsmash.uberserver.LobbyGameSpeed;
import net.warsmash.uberserver.LobbyPlayerType;

public class DefaultGamingNetworkServerClientBuilder implements GamingNetworkServerClientBuilder {
	private final GamingNetworkServerBusinessLogicImpl businessLogicImpl;

	public DefaultGamingNetworkServerClientBuilder(final GamingNetworkServerBusinessLogicImpl businessLogicImpl) {
		this.businessLogicImpl = businessLogicImpl;
	}

	@Override
	public GamingNetworkClientToServerListener createClient(final WritableOutput output) {
		final GamingNetworkServerToClientWriter writer = new GamingNetworkServerToClientWriter(output);
		return new GamingNetworkClientToServerListener() {
			@Override
			public void disconnected() {
				DefaultGamingNetworkServerClientBuilder.this.businessLogicImpl.disconnected(writer);
			}

			@Override
			public void handshake(final String gameId, final int version) {
				DefaultGamingNetworkServerClientBuilder.this.businessLogicImpl.handshake(gameId, version, writer);
			}

			@Override
			public void login(final String username, final char[] passwordHash) {
				DefaultGamingNetworkServerClientBuilder.this.businessLogicImpl.login(username, passwordHash, writer);
			}

			@Override
			public void joinChannel(final long sessionToken, final String channelName) {
				DefaultGamingNetworkServerClientBuilder.this.businessLogicImpl.joinChannel(sessionToken, channelName,
						writer);
			}

			@Override
			public void joinGame(final long sessionToken, final String gameName) {
				DefaultGamingNetworkServerClientBuilder.this.businessLogicImpl.joinGame(sessionToken, gameName, writer);
			}

			@Override
			public void emoteMessage(final long sessionToken, final String text) {
				DefaultGamingNetworkServerClientBuilder.this.businessLogicImpl.emoteMessage(sessionToken, text, writer);
			}

			@Override
			public void createAccount(final String username, final char[] passwordHash) {
				DefaultGamingNetworkServerClientBuilder.this.businessLogicImpl.createAccount(username, passwordHash,
						writer);
			}

			@Override
			public void chatMessage(final long sessionToken, final String text) {
				DefaultGamingNetworkServerClientBuilder.this.businessLogicImpl.chatMessage(sessionToken, text, writer);
			}

			@Override
			public void queryGamesList(final long sessionToken) {
				DefaultGamingNetworkServerClientBuilder.this.businessLogicImpl.queryGamesList(sessionToken, writer);
			}

			@Override
			public void queryGameInfo(final long sessionToken, final String gameName) {
				DefaultGamingNetworkServerClientBuilder.this.businessLogicImpl.queryGameInfo(sessionToken, writer);
			}

			@Override
			public void createGame(final long sessionToken, final String gameName, final String mapName,
					final int totalSlots, final LobbyGameSpeed gameSpeed, final HostedGameVisibility visibility,
					long mapChecksum) {
				DefaultGamingNetworkServerClientBuilder.this.businessLogicImpl.createGame(sessionToken, gameName,
						mapName, totalSlots, gameSpeed, visibility, mapChecksum, writer);
			}

			@Override
			public void leaveGame(long sessionToken) {
				DefaultGamingNetworkServerClientBuilder.this.businessLogicImpl.leaveGame(sessionToken, writer);
			}

			@Override
			public void uploadMapData(long sessionToken, int sequenceNumber, ByteBuffer data) {
				DefaultGamingNetworkServerClientBuilder.this.businessLogicImpl.uploadMapData(sessionToken,
						sequenceNumber, data, writer);
			}

			@Override
			public void mapDone(long sessionToken, int sequenceNumber) {
				DefaultGamingNetworkServerClientBuilder.this.businessLogicImpl.mapDone(sessionToken, sequenceNumber,
						writer);
			}

			@Override
			public void requestMap(long sessionToken) {
				DefaultGamingNetworkServerClientBuilder.this.businessLogicImpl.requestMap(sessionToken, writer);
			}

			@Override
			public void gameLobbySetPlayerSlot(long sessionToken, int slot, LobbyPlayerType lobbyPlayerType) {
				DefaultGamingNetworkServerClientBuilder.this.businessLogicImpl.gameLobbySetPlayerSlot(sessionToken,
						slot, lobbyPlayerType, writer);
			}

			@Override
			public void gameLobbySetPlayerRace(long sessionToken, int slot, int raceItemIndex) {
				DefaultGamingNetworkServerClientBuilder.this.businessLogicImpl.gameLobbySetPlayerRace(sessionToken,
						slot, raceItemIndex, writer);
			}
		};
	}

}
