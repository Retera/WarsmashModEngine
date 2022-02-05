package com.etheller.warsmash.networking.uberserver;

import net.warsmash.nio.channels.WritableOutput;
import net.warsmash.uberserver.GamingNetworkClientToServerListener;

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
		};
	}

}
