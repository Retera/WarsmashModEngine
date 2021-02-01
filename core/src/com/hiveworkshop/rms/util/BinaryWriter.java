package com.hiveworkshop.rms.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BinaryWriter {
	public ByteBuffer buffer;

	public BinaryWriter(final int capacity) {
		this.buffer = ByteBuffer.allocate(capacity);
		this.buffer.order(ByteOrder.LITTLE_ENDIAN);
	}

	public int remaining() {
		return this.buffer.remaining();
	}

	public int position() {
		return this.buffer.position();
	}

	public void position(final int newPosition) {
		this.buffer.position(newPosition);
	}

	public void move(final int offset) {
		this.buffer.position(this.buffer.position() + offset);
	}

	public void write(final String value) {
		writeInt8Array(value.getBytes());
	}

	public void writeWithNulls(final String value, final int length) {
		final byte[] bytes = value.getBytes();
		final int nulls = length - bytes.length;

		writeInt8Array(bytes);

		if (nulls > 0) {
			for (int i = 0; i < nulls; i++) {
				writeInt8((byte) 0);
			}
		}
	}

	public void writeInt8(final byte value) {
		this.buffer.put(value);
	}

	public void writeInt16(final short value) {
		this.buffer.putShort(value);
	}

	public void writeInt32(final int value) {
		this.buffer.putInt(value);
	}

	public void writeInt64(final long value) {
		this.buffer.putLong(value);
	}

	public void writeUInt8(final short value) {
		this.buffer.put((byte) value);
	}

	public void writeUInt16(final int value) {
		this.buffer.putShort((short) value);
	}

	public void writeUInt32(final long value) {
		this.buffer.putInt((int) value);
	}

	public void writeFloat32(final float value) {
		this.buffer.putFloat(value);
	}

	public void writeFloat64(final double value) {
		this.buffer.putDouble(value);
	}

	public void writeInt8Array(final byte[] values) {
		for (final byte value : values) {
			writeInt8(value);
		}
	}

	public void writeInt16Array(final short[] values) {
		for (final short value : values) {
			writeInt16(value);
		}
	}

	public void writeInt32Array(final int[] values) {
		for (final int value : values) {
			writeInt32(value);
		}
	}

	public void writeInt64Array(final long[] values) {
		for (final long value : values) {
			writeInt64(value);
		}
	}

	public void writeUInt8Array(final short[] values) {
		for (final short value : values) {
			writeUInt8(value);
		}
	}

	public void writeUInt16Array(final int[] values) {
		for (final int value : values) {
			writeUInt16(value);
		}
	}

	public void writeUInt32Array(final long[] values) {
		for (final long value : values) {
			writeUInt32(value);
		}
	}

	public void writeFloat32Array(final float[] values) {
		for (final float value : values) {
			writeFloat32(value);
		}
	}

	public void writeFloat64Array(final double[] values) {
		for (final double value : values) {
			writeFloat64(value);
		}
	}

	public void writeTag(final int tag) {
		writeInt32(Integer.reverseBytes(tag));
	}
}
