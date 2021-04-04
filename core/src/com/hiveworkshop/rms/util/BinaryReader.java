package com.hiveworkshop.rms.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BinaryReader {
	ByteBuffer buffer;

	public BinaryReader(final ByteBuffer buffer) {
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.position(0);

		this.buffer = buffer;
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

	public String read(final int count) {
		final StringBuilder value = new StringBuilder();

		for (int i = 0; i < count; i++) {
			final byte b = this.buffer.get();

			if (b != 0) {
				value.append((char) (b & 0xFF));
			}
		}

		return value.toString();
	}

	public String readBytes(final int count) {
		final StringBuilder value = new StringBuilder();

		for (int i = 0; i < count; i++) {
			value.append((char) (this.buffer.get() & 0xFF));
		}

		return value.toString();
	}

	public String readUntilNull() {
		final StringBuilder value = new StringBuilder();
		byte b = this.buffer.get();

		while (b != 0) {
			value.append((char) (b & 0xFF));

			b = this.buffer.get();
		}

		return value.toString();
	}

	public byte readInt8() {
		return this.buffer.get();
	}

	public short readInt16() {
		return this.buffer.getShort();
	}

	public int readInt32() {
		return this.buffer.getInt();
	}

	public long readInt64() {
		return this.buffer.getLong();
	}

	public short readUInt8() {
		return (short) Byte.toUnsignedInt(this.buffer.get());
	}

	public int readUInt16() {
		return Short.toUnsignedInt(this.buffer.getShort());
	}

	public long readUInt32() {
		return Integer.toUnsignedLong(this.buffer.getInt());
	}

	public float readFloat32() {
		return this.buffer.getFloat();
	}

	public double readFloat64() {
		return this.buffer.getDouble();
	}

	public byte[] readInt8Array(final byte[] out) {
		for (int i = 0, l = out.length; i < l; i++) {
			out[i] = readInt8();
		}

		return out;
	}

	public byte[] readInt8Array(final int count) {
		return readInt8Array(new byte[count]);
	}

	public short[] readInt16Array(final short[] out) {
		for (int i = 0, l = out.length; i < l; i++) {
			out[i] = readInt16();
		}

		return out;
	}

	public short[] readInt16Array(final int count) {
		return readInt16Array(new short[count]);
	}

	public int[] readInt32Array(final int[] out) {
		for (int i = 0, l = out.length; i < l; i++) {
			out[i] = readInt32();
		}

		return out;
	}

	public int[] readInt32Array(final int count) {
		return readInt32Array(new int[count]);
	}

	public long[] readInt64Array(final long[] out) {
		for (int i = 0, l = out.length; i < l; i++) {
			out[i] = readInt64();
		}

		return out;
	}

	public long[] readInt64Array(final int count) {
		return readInt64Array(new long[count]);
	}

	public short[] readUInt8Array(final short[] out) {
		for (int i = 0, l = out.length; i < l; i++) {
			out[i] = readUInt8();
		}

		return out;
	}

	public short[] readUInt8Array(final int count) {
		return readUInt8Array(new short[count]);
	}

	public int[] readUInt16Array(final int[] out) {
		for (int i = 0, l = out.length; i < l; i++) {
			out[i] = readUInt16();
		}

		return out;
	}

	public int[] readUInt16Array(final int count) {
		return readUInt16Array(new int[count]);
	}

	public long[] readUInt32Array(final long[] out) {
		for (int i = 0, l = out.length; i < l; i++) {
			out[i] = readUInt32();
		}

		return out;
	}

	public long[] readUInt32Array(final int count) {
		return readUInt32Array(new long[count]);
	}

	public float[] readFloat32Array(final float[] out) {
		for (int i = 0, l = out.length; i < l; i++) {
			out[i] = readFloat32();
		}

		return out;
	}

	public float[] readFloat32Array(final int count) {
		return readFloat32Array(new float[count]);
	}

	public double[] readFloat64Array(final double[] out) {
		for (int i = 0, l = out.length; i < l; i++) {
			out[i] = readFloat64();
		}

		return out;
	}

	public double[] readFloat64Array(final int count) {
		return readFloat64Array(new double[count]);
	}

	public int readTag() {
		return Integer.reverseBytes(readInt32());
	}
}
