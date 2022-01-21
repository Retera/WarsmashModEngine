package net.warsmash.nio.channels.tcp;

import net.warsmash.nio.channels.ByteParser;

public interface TCPClientParser extends ByteParser {
	void disconnected();
}
