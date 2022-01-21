package net.warsmash.nio.channels;

import java.nio.ByteBuffer;

public interface ByteParser {
	void parse(ByteBuffer data);
}
