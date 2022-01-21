package net.warsmash.nio.channels;

import java.nio.ByteBuffer;

public interface WritableOutput extends OpenedChannel {
	void write(ByteBuffer data);
}
