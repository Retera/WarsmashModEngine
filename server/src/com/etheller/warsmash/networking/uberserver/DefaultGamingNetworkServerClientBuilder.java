package com.etheller.warsmash.networking.uberserver;

import net.warsmash.nio.channels.WritableOutput;
import net.warsmash.uberserver.GamingNetworkClientToServerListener;
import net.warsmash.uberserver.HostedGameVisibility;
import net.warsmash.uberserver.LobbyGameSpeed;

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
					final int totalSlots, final LobbyGameSpeed gameSpeed, final long gameCreationTimeMillis,
					final HostedGameVisibility visibility) {
				throw new UnsupportedOperationException();
			}
		};
	}

}
