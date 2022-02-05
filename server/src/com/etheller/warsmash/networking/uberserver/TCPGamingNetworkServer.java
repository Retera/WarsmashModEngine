package com.etheller.warsmash.networking.uberserver;

import java.net.SocketAddress;
import java.nio.ByteOrder;

import net.warsmash.nio.channels.ChannelOpener;
import net.warsmash.nio.channels.SocketChannelCallback;
import net.warsmash.nio.channels.WritableOutput;
import net.warsmash.nio.channels.tcp.TCPClientParser;
import net.warsmash.nio.util.ExceptionListener;
import net.warsmash.uberserver.GamingNetwork;

public class TCPGamingNetworkServer {
	private final ChannelOpener channelOpener;
	private final GamingNetworkServerClientBuilder gamingNetworkServerClientBuilder;

	public TCPGamingNetworkServer(final ChannelOpener channelOpener,
			final GamingNetworkServerClientBuilder gamingNetworkServerClientBuilder) {
		this.channelOpener = channelOpener;
		this.gamingNetworkServerClientBuilder = gamingNetworkServerClientBuilder;
	}

	public void start() {
		this.channelOpener.openTCPServerChannel(GamingNetwork.PORT, new SocketChannelCallback() {
			@Override
			public TCPClientParser onConnect(final WritableOutput writableOpenedChannel,
					final SocketAddress remoteAddress) {
				System.out.println("Received connection from " + remoteAddress);
				return new TCPGamingNetworkServerClientParser(
						TCPGamingNetworkServer.this.gamingNetworkServerClientBuilder
								.createClient(writableOpenedChannel));
			}
		}, ExceptionListener.THROW_RUNTIME, 8 * 1024 * 1024, ByteOrder.BIG_ENDIAN);
	}

}
