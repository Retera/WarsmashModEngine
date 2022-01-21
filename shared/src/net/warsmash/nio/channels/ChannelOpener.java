package net.warsmash.nio.channels;

import java.net.SocketAddress;
import java.nio.ByteOrder;

import net.warsmash.networking.udp.UdpServerListener;
import net.warsmash.nio.channels.tcp.TCPClientParser;
import net.warsmash.nio.util.ExceptionListener;

public interface ChannelOpener {
	OpenedChannel openUDPServerChannel(int port, UdpServerListener listener, ExceptionListener exceptionListener,
			int bufferSize, ByteOrder byteOrder);

	OpenedChannel openTCPServerChannel(int port, SocketChannelCallback callback,
			final ExceptionListener exceptionListener, final int bufferSize, ByteOrder byteOrder);

	WritableOutput openTCPClientChannel(SocketAddress socketAddress, TCPClientParser tcpClientParser,
			ExceptionListener exceptionListener, int bufferSize, ByteOrder byteOrder);
}
