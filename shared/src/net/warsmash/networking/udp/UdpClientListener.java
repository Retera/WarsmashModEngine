package net.warsmash.networking.udp;

import java.nio.ByteBuffer;

public interface UdpClientListener {
	void parse(ByteBuffer buffer);
}
