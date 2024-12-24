package net.warsmash.nio.channels;

import java.net.SocketAddress;

import net.warsmash.nio.channels.tcp.TCPClientParser;

public interface SocketChannelCallback {
	TCPClientParser onConnect(WritableSocketOutput writableOpenedChannel, SocketAddress remoteAddress);
}
