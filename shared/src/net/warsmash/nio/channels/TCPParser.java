package net.warsmash.nio.channels;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

public interface TCPParser {
	void parse(SocketAddress sourceAddress, ByteBuffer data);
}
