package com.hiveworkshop.blizzard.casc.storage;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.hiveworkshop.blizzard.casc.nio.MalformedCASCStructureException;
import com.hiveworkshop.lang.Hex;

/**
 * BLTE content entry, used to decode BLTE file data that follows it.
 */
public class BLTEContent {
	/**
	 * BLTE content identifier.
	 */
	private static final ByteBuffer IDENTIFIER = ByteBuffer.wrap(new byte[] { 'B', 'L', 'T', 'E' });

	/**
	 * Hash length in bytes. Should be fetched from appropriate digest length.
	 */
	private static final int HASH_LENGTH = 16;

	private final long compressedSize;
	private final long decompressedSize;
	private final byte[] hash = new byte[HASH_LENGTH];

	public BLTEContent(final ByteBuffer blteBuffer) {
		compressedSize = Integer.toUnsignedLong(blteBuffer.getInt());
		decompressedSize = Integer.toUnsignedLong(blteBuffer.getInt());
		blteBuffer.get(hash);
	}

	public static BLTEContent[] decodeContent(final ByteBuffer storageBuffer) throws IOException {
		final ByteBuffer contentBuffer = storageBuffer.slice();

		// check identifier

		if ((contentBuffer.remaining() < IDENTIFIER.remaining())
				|| !contentBuffer.limit(IDENTIFIER.remaining()).equals(IDENTIFIER)) {
			throw new MalformedCASCStructureException("missing BLTE identifier");
		}

		// decode header

		contentBuffer.limit(contentBuffer.capacity());
		contentBuffer.position(contentBuffer.position() + IDENTIFIER.remaining());
		contentBuffer.order(ByteOrder.BIG_ENDIAN);

		final long headerSize;
		try {
			headerSize = Integer.toUnsignedLong(contentBuffer.getInt());
		} catch (final BufferUnderflowException e) {
			throw new MalformedCASCStructureException("header preamble goes out of bounds");
		}

		if (headerSize == 0L) {
			storageBuffer.position(storageBuffer.position() + contentBuffer.position());
			return new BLTEContent[0];
		} else if (headerSize > contentBuffer.capacity()) {
			throw new MalformedCASCStructureException("BLTE header extends beyond storage buffer bounds");
		}

		contentBuffer.limit((int) headerSize);
		final ByteBuffer blteBuffer = contentBuffer.slice();
		blteBuffer.order(ByteOrder.BIG_ENDIAN);
		contentBuffer.position(contentBuffer.limit());
		contentBuffer.limit(contentBuffer.capacity());

		final byte flags;
		final int entryCount;
		try {
			flags = blteBuffer.get();
			if (flags != 0xF) {
				throw new MalformedCASCStructureException("unknown flags");
			}
			// BE24 read
			final int be24Bytes = 3;
			final ByteBuffer be24Buffer = ByteBuffer.allocate(Integer.BYTES);
			be24Buffer.order(ByteOrder.BIG_ENDIAN);
			blteBuffer.get(be24Buffer.array(), Integer.BYTES - be24Bytes, be24Bytes);
			entryCount = be24Buffer.getInt(0);
			if (entryCount == 0) {
				throw new MalformedCASCStructureException("explicit zero entry count");
			}
		} catch (final BufferUnderflowException e) {
			throw new MalformedCASCStructureException("header goes out of bounds");
		}

		final BLTEContent[] content = new BLTEContent[entryCount];

		for (int index = 0; index < content.length; index += 1) {
			content[index] = new BLTEContent(blteBuffer);
		}

		if (blteBuffer.hasRemaining()) {
			throw new MalformedCASCStructureException("unprocessed BLTE bytes");
		}

		storageBuffer.position(storageBuffer.position() + contentBuffer.position());

		return content;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("BLTEChunk{compressedSize=");
		builder.append(compressedSize);
		builder.append(", decompressedSize=");
		builder.append(decompressedSize);
		builder.append(", hash=");
		Hex.stringBufferAppendHex(builder, hash);
		builder.append("}");

		return builder.toString();
	}

	public long getCompressedSize() {
		return compressedSize;
	}

	public long getDecompressedSize() {
		return decompressedSize;
	}

	public byte[] getHash() {
		return hash.clone();
	}

}
