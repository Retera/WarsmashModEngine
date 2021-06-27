package com.etheller.warsmash.networking.udp;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

public interface UdpServerListener {
	void parse(SocketAddress sourceAddress, ByteBuffer buffer);
}
