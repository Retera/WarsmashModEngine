package com.etheller.warsmash.networking.uberserver;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import net.warsmash.networking.udp.UdpServerListener;
import net.warsmash.nio.channels.ChannelOpener;
import net.warsmash.nio.channels.SocketChannelCallback;
import net.warsmash.nio.channels.WritableOutput;
import net.warsmash.nio.channels.WritableSocketOutput;
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
			public TCPClientParser onConnect(final WritableSocketOutput writableOpenedChannel,
					final SocketAddress remoteAddress) {
				System.out.println("Received connection from " + remoteAddress);
				return new TCPGamingNetworkServerClientParser(
						TCPGamingNetworkServer.this.gamingNetworkServerClientBuilder
								.createClient(writableOpenedChannel));
			}
		}, new ExceptionListener() {
			@Override
			public void caught(final Exception e) {
				e.printStackTrace();
			}
		}, 8 * 1024 * 1024, ByteOrder.LITTLE_ENDIAN);

		this.channelOpener.openUDPServerChannel(GamingNetwork.PORT, new UdpServerListener() {
			@Override
			public void parse(SocketAddress sourceAddress, ByteBuffer buffer) {

			}
		}, new ExceptionListener() {

			@Override
			public void caught(Exception e) {
				e.printStackTrace();
			}
		}, 8 * 1024 * 1024, ByteOrder.LITTLE_ENDIAN);
	}

}
