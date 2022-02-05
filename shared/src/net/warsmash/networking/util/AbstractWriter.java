package net.warsmash.networking.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import net.warsmash.nio.channels.WritableOutput;

public class AbstractWriter {
	private final WritableOutput writableOutput;
	protected final ByteBuffer writeBuffer;

	public AbstractWriter(final WritableOutput writableOutput) {
		this.writableOutput = writableOutput;
		this.writeBuffer = ByteBuffer.allocateDirect(8 * 1024).order(ByteOrder.LITTLE_ENDIAN);
		this.writeBuffer.clear();
	}

	private void ensureCapacity(final int length) {
		if (this.writeBuffer.remaining() < length) {
			send();
		}
	}

	protected final void beginMessage(final int protocol, final int length) {
		ensureCapacity(length + 4 + 4);
		this.writeBuffer.putInt(protocol);
		this.writeBuffer.putInt(length);
	}

	protected final void send() {
		this.writeBuffer.flip();
		this.writableOutput.write(this.writeBuffer);
		this.writeBuffer.clear();
	}

}
