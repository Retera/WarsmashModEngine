package com.etheller.warsmash.parsers.mdlx;

import java.io.IOException;

import com.etheller.warsmash.util.ParseUtils;
import com.etheller.warsmash.util.War3ID;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class UnknownChunk implements Chunk {
	private final short[] chunk;
	private final War3ID tag;

	public UnknownChunk(final LittleEndianDataInputStream stream, final long size, final War3ID tag)
			throws IOException {
		System.err.println("Loading unknown chunk: " + tag);
		this.chunk = ParseUtils.readUInt8Array(stream, (int) size);
		this.tag = tag;
	}

	public void writeMdx(final LittleEndianDataOutputStream stream) throws IOException {
		ParseUtils.writeWar3ID(stream, this.tag);
		// Below: Byte.BYTES used because it's mean as a UInt8 array. This is
		// not using Short.BYTES, deliberately, despite using a short[] as the
		// type for the array. This is a Java problem that did not exist in the original
		// JavaScript implementation by Ghostwolf
		ParseUtils.writeUInt32(stream, this.chunk.length * Byte.BYTES);
		ParseUtils.writeUInt8Array(stream, this.chunk);
	}

	@Override
	public long getByteLength() {
		return 8 + (this.chunk.length * Byte.BYTES);
	}
}
