package com.etheller.warsmash.networking.uberserver;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import net.warsmash.nio.channels.ChannelOpener;
import net.warsmash.nio.channels.SocketChannelCallback;
import net.warsmash.nio.channels.WritableOutput;
import net.warsmash.nio.channels.tcp.TCPClientParser;
import net.warsmash.nio.util.ExceptionListener;
import net.warsmash.uberserver.GamingNetwork;

public class TCPGamingNetworkServer {
	private final ChannelOpener channelOpener;

	public TCPGamingNetworkServer(final ChannelOpener channelOpener) {
		this.channelOpener = channelOpener;
	}

	public void start() {
		this.channelOpener.openTCPServerChannel(GamingNetwork.PORT, new SocketChannelCallback() {
			@Override
			public TCPClientParser onConnect(final WritableOutput writableOpenedChannel,
					final SocketAddress remoteAddress) {
				System.out.println("Received connection from " + remoteAddress);
				return new TCPClientParser() {
					@Override
					public void parse(final ByteBuffer data) {
						System.out.println("Got " + data.remaining() + " bytes from " + remoteAddress);
						if (data.hasRemaining()) {
							System.out.print("[");
							System.out.print(data.get());
							while (data.hasRemaining()) {
								System.out.print(", ");
								System.out.print(data.get());
							}
							System.out.println("]");
						}
						writableOpenedChannel.write(ByteBuffer.wrap("reply".getBytes()));
					}

					@Override
					public void disconnected() {
						System.out.println("Disconnected from " + remoteAddress);
					}
				};
			}
		}, ExceptionListener.THROW_RUNTIME, 8 * 1024 * 1024, ByteOrder.BIG_ENDIAN);
	}

}
