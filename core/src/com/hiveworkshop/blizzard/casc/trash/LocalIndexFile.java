package com.hiveworkshop.blizzard.casc.trash;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import com.hiveworkshop.ReteraCASCUtils;
import com.hiveworkshop.blizzard.casc.nio.HashMismatchException;
import com.hiveworkshop.blizzard.casc.nio.LittleHashBlockProcessor;
import com.hiveworkshop.lang.Hex;

public class LocalIndexFile {
	private byte bucketIndex;

	private byte entryFileSizeLength;

	private byte entryDataOffsetLength;

	private byte entryKeyLength;

	private byte dataFileSizeBits;

	private long dataSizeMaximum;

	private final ArrayList<IndexEntry> entries = new ArrayList<>();

	public static int getIndexNumber(final byte[] key, final int keyLength) {
		int accumulator = 0;
		for (int i = 0; i < keyLength; i += 1) {
			accumulator ^= key[i];
		}
		final int nibbleMask = (1 << 4) - 1;
		return accumulator & nibbleMask ^ accumulator >> 4 & nibbleMask;
	}

	public LocalIndexFile(final ByteBuffer encodedFileBuffer) throws IOException {
		decode(encodedFileBuffer);
	}

	public void decode(final ByteBuffer encodedFileBuffer) throws IOException {
		final LittleHashBlockProcessor hashBlockProcessor = new LittleHashBlockProcessor();
		final int fileLength = encodedFileBuffer.limit();

		final int headerLength = hashBlockProcessor.processBlock(encodedFileBuffer);
		if (headerLength < 0) {
			throw new HashMismatchException("index header corrupt");
		}

		encodedFileBuffer.limit(encodedFileBuffer.position() + headerLength);
		encodedFileBuffer.order(ByteOrder.LITTLE_ENDIAN);

		if (encodedFileBuffer.getShort() != 7) {
			// possibly malformed
		}
		bucketIndex = encodedFileBuffer.get();
		if (encodedFileBuffer.get() != 0) {
			// possibly malformed
		}
		entryFileSizeLength = encodedFileBuffer.get();
		entryDataOffsetLength = encodedFileBuffer.get();
		entryKeyLength = encodedFileBuffer.get();
		dataFileSizeBits = encodedFileBuffer.get();
		dataSizeMaximum = encodedFileBuffer.getLong();

		encodedFileBuffer.limit(fileLength);
		final int entriesAlignmentMask = (1 << 4) - 1;
		encodedFileBuffer.position((encodedFileBuffer.position() + entriesAlignmentMask) & ~entriesAlignmentMask);

		final int entriesLength = hashBlockProcessor.processBlock(encodedFileBuffer);
		if (entriesLength < 0) {
			throw new HashMismatchException("index entries corrupt");
		}

		encodedFileBuffer.limit(encodedFileBuffer.position() + entriesLength);
		final int entryLength = entryFileSizeLength + entryDataOffsetLength + entryKeyLength;
		final int entryCount = encodedFileBuffer.remaining() / entryLength;

		entries.ensureCapacity(entryCount);

		final ByteBuffer decodeDataOffsetBuffer = ByteBuffer.allocate(Long.BYTES);
		final int decodeDataOffsetOffset = Long.BYTES - entryDataOffsetLength;
		final ByteBuffer decodeFileSizeBuffer = ByteBuffer.allocate(Long.BYTES);
		decodeFileSizeBuffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < entryCount; i += 1) {
			final IndexEntry entry = new IndexEntry();

			entry.key = new byte[entryKeyLength];
			encodedFileBuffer.get(entry.key);

			encodedFileBuffer.get(decodeDataOffsetBuffer.array(), decodeDataOffsetOffset, entryDataOffsetLength);
			entry.dataOffset = decodeDataOffsetBuffer.getLong(0);

			encodedFileBuffer.get(decodeFileSizeBuffer.array(), 0, entryFileSizeLength);
			entry.fileSize = decodeFileSizeBuffer.getLong(0);

			// this can be used to detect special cross linking entries
			// if (getIndexNumber(entry.key, entry.key.length) != bucketIndex);
			// System.out.println("Bad key index: index=" + i + ", entry=" + entry + ",
			// bucket=" + getIndexNumber(entry.key, entry.key.length));

			entries.add(entry);
		}

		encodedFileBuffer.limit(fileLength);
	}

	public IndexEntry getEntry(final byte[] key) {
		for (final LocalIndexFile.IndexEntry indexEntry : entries) {
			if (ReteraCASCUtils.arraysEquals(indexEntry.key, 0, entryKeyLength, key, 0, entryKeyLength)) {
				return indexEntry;
			}
		}

		return null;
	}

	public IndexEntry getEntry(final int index) {
		return entries.get(index);
	}

	public int getEntryCount() {
		return entries.size();
	}

	public long getDataFileOffset(final long dataOffset) {
		return dataOffset & (1L << dataFileSizeBits) - 1L;
	}

	public int getDataFileNumber(final long dataOffset) {
		return (int) (dataOffset >>> dataFileSizeBits);
	}

	public static class IndexEntry {
		private byte[] key;
		private long dataOffset;
		private long fileSize;

		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append("IndexEntry{key=0x");
			Hex.stringBufferAppendHex(builder, key);
			builder.append(", dataOffset=");
			builder.append(dataOffset);
			builder.append(", fileSize=");
			builder.append(fileSize);
			builder.append("}");

			return builder.toString();
		}

		public long getDataOffset() {
			return dataOffset;
		}

		public long getFileSize() {
			return fileSize;
		}

		public String getKeyString() {
			final StringBuilder builder = new StringBuilder();
			Hex.stringBufferAppendHex(builder, key);
			return builder.toString();
		}

		public boolean compareKey(final byte[] otherKey) {
			return ReteraCASCUtils.arraysEquals(key, 0, key.length, otherKey, 0, key.length);
		}
	}
}
