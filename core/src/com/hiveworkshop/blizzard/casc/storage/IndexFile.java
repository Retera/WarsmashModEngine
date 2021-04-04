package com.hiveworkshop.blizzard.casc.storage;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;

import com.hiveworkshop.blizzard.casc.Key;
import com.hiveworkshop.blizzard.casc.nio.HashMismatchException;
import com.hiveworkshop.blizzard.casc.nio.LittleHashBlockProcessor;
import com.hiveworkshop.blizzard.casc.nio.MalformedCASCStructureException;

public class IndexFile {
	/**
	 * Alignment of the index entry block in bytes.
	 */
	private static final int ENTRY_BLOCK_ALIGNMENT = 16;

	private int bucketIndex;

	private int fileSizeLength;

	private int dataOffsetLength;

	private int encodingKeyLength;

	private int dataFileSizeBits;

	private long dataSizeMaximum;

	private final ArrayList<IndexEntry> entries = new ArrayList<>();

	public IndexFile(final ByteBuffer fileBuffer) throws IOException {
		decode(fileBuffer);
	}

	private void decode(final ByteBuffer fileBuffer) throws IOException {
		final ByteBuffer sourceBuffer = fileBuffer.slice();

		// decode header

		final LittleHashBlockProcessor hashBlockProcessor = new LittleHashBlockProcessor();

		final ByteBuffer headerBuffer;
		try {
			headerBuffer = hashBlockProcessor.getBlock(sourceBuffer);
		} catch (final HashMismatchException e) {
			throw new MalformedCASCStructureException("header block corrupt", e);
		}

		headerBuffer.order(ByteOrder.LITTLE_ENDIAN);

		try {
			if (headerBuffer.getShort() != 7) {
				// possibly malformed
			}
			bucketIndex = Byte.toUnsignedInt(headerBuffer.get());
			if (headerBuffer.get() != 0) {
				// possibly malformed
			}
			fileSizeLength = Byte.toUnsignedInt(headerBuffer.get());
			dataOffsetLength = Byte.toUnsignedInt(headerBuffer.get());
			encodingKeyLength = Byte.toUnsignedInt(headerBuffer.get());
			dataFileSizeBits = Byte.toUnsignedInt(headerBuffer.get());
			dataSizeMaximum = headerBuffer.getLong();
		} catch (final BufferUnderflowException e) {
			throw new MalformedCASCStructureException("header block too small");
		}

		// decode entries

		final int entriesAlignmentMask = ENTRY_BLOCK_ALIGNMENT - 1;
		sourceBuffer.position((sourceBuffer.position() + entriesAlignmentMask) & ~entriesAlignmentMask);

		final ByteBuffer entryBuffer;
		try {
			entryBuffer = hashBlockProcessor.getBlock(sourceBuffer);
		} catch (final HashMismatchException e) {
			throw new MalformedCASCStructureException("entries block corrupt", e);
		}

		final int entryLength = fileSizeLength + dataOffsetLength + encodingKeyLength;
		final int entryCount = entryBuffer.remaining() / entryLength;

		entries.ensureCapacity(entryCount);

		final ByteBuffer decodeDataOffsetBuffer = ByteBuffer.allocate(Long.BYTES);
		final int decodeDataOffsetOffset = Long.BYTES - dataOffsetLength;
		final ByteBuffer decodeFileSizeBuffer = ByteBuffer.allocate(Long.BYTES);
		decodeFileSizeBuffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < entryCount; i += 1) {
			final byte[] key = new byte[encodingKeyLength];
			entryBuffer.get(key);

			entryBuffer.get(decodeDataOffsetBuffer.array(), decodeDataOffsetOffset, dataOffsetLength);
			final long dataOffset = decodeDataOffsetBuffer.getLong(0);

			entryBuffer.get(decodeFileSizeBuffer.array(), 0, fileSizeLength);
			final long fileSize = decodeFileSizeBuffer.getLong(0);

			// this can be used to detect special cross linking entries
			// if (getIndexNumber(entry.key, entry.key.length) != bucketIndex);
			// System.out.println("Bad key index: index=" + i + ", entry=" + entry + ",
			// bucket=" + getIndexNumber(entry.key, entry.key.length));

			entries.add(new IndexEntry(key, dataOffset, fileSize));
		}

		if (entryBuffer.hasRemaining()) {
			throw new MalformedCASCStructureException("unable to fully process entries block");
		}

		fileBuffer.position(fileBuffer.position() + sourceBuffer.position());
	}

	public int getBucketIndex() {
		return bucketIndex;
	}

	public int getStoreIndex(final long dataOffset) {
		return (int) (dataOffset >>> dataFileSizeBits);
	}

	public long getStoreOffset(final long dataOffset) {
		return dataOffset & ((1L << dataFileSizeBits) - 1L);
	}

	public long getDataSizeMaximum() {
		return dataSizeMaximum;
	}

	public IndexEntry getEntry(final Key encodingKey) {
		final int index = Collections.binarySearch(entries, encodingKey, (left, right) -> {
			if ((left instanceof IndexEntry) && (right instanceof Key)) {
				final IndexEntry entry = (IndexEntry) left;
				final Key ekey = (Key) right;
				return entry.getKey().compareTo(ekey);
			}
			throw new IllegalArgumentException("binary search comparing in inverted order");
		});

		return index >= 0 ? entries.get(index) : null;
	}

	public IndexEntry getEntry(final int index) {
		return entries.get(index);
	}

	public int getEntryCount() {
		return entries.size();
	}

	public int getEncodingKeyLength() {
		return encodingKeyLength;
	}

}
