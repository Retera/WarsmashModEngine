package com.hiveworkshop.blizzard.casc.trash;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import com.hiveworkshop.blizzard.casc.nio.HashMismatchException;
import com.hiveworkshop.blizzard.casc.nio.MalformedCASCStructureException;
import com.hiveworkshop.lang.Hex;

public class LocalDataFiles implements Closeable {
	private static final int FRAGMENTATION_SIZE_BITS = 30;

	private static final int FILE_ENTRY_HEADER_SIZE = 30;

	private static final byte[] BLTE_MIME = new byte[] { 'B', 'L', 'T', 'E' };

	private final HashMap<Integer, FileChannel> dataFiles = new HashMap<Integer, FileChannel>();

	public LocalDataFiles(final Path dataPath) throws IOException {
		mountDataFiles(dataPath);
	}

	public void mountDataFiles(final Path dataPath) throws IOException {
		try (final DirectoryStream<Path> dataFileIterator = Files.newDirectoryStream(dataPath, "data.*")) {
			for (final Path dataFile : dataFileIterator) {
				System.out.println(dataFile);
				final String fileName = dataFile.getFileName().toString();
				final int number = Integer.parseInt(fileName.substring(5, 8));
				dataFiles.put(number, FileChannel.open(dataFile, StandardOpenOption.READ));
			}

		} catch (final Exception e) {
			close();
		}
	}

	@Override
	public void close() throws IOException {
		IOException exception = null;
		for (final Map.Entry<Integer, FileChannel> dataFileEntry : dataFiles.entrySet()) {
			try {
				dataFileEntry.getValue().close();
			} catch (final IOException e) {
				exception = e;
			}
		}

		if (exception != null) {
			throw new IOException("one or more IOExceptions occured during closure", exception);
		}
	}

	public FileEntry getFileEntry(final LocalIndexFile.IndexEntry indexEntry) throws IOException {
		final ByteBuffer fileHeader = ByteBuffer.allocate(FILE_ENTRY_HEADER_SIZE);

		final long dataOffset = indexEntry.getDataOffset();
		final int dataFile = (int) (dataOffset >>> FRAGMENTATION_SIZE_BITS);
		final long fileOffset = dataOffset & (1L << FRAGMENTATION_SIZE_BITS) - 1L;
		final FileChannel channel = dataFiles.get(dataFile);
		if (channel.read(fileHeader, fileOffset) != fileHeader.limit()) {
			throw new EOFException("unexpected incomplete read");
		}
		fileHeader.flip();

		fileHeader.order(ByteOrder.LITTLE_ENDIAN);
		final FileEntry fileEntry = new FileEntry();
		fileEntry.dataFile = dataFile;
		fileEntry.fileOffset = fileOffset;

		// key is in reversed byte order
		final byte[] key = new byte[16];
		int keyPos = fileHeader.position() + key.length;
		fileHeader.position(keyPos);
		for (int i = 0; i < key.length; i++) {
			key[i] = fileHeader.get(--keyPos);
		}
		if (!indexEntry.compareKey(key)) {
			throw new HashMismatchException("file entry does not match index entry");
		}
		fileEntry.key = key;

		fileEntry.size = Integer.toUnsignedLong(fileHeader.getInt());
		fileEntry.flags = fileHeader.getShort();

		// TODO actually check these
		final int ChecksumA = fileHeader.getInt();
		final int ChecksumB = fileHeader.getInt();

		return fileEntry;
	}

	public BLTEChunk[] getBLTEChunks(final FileEntry file) throws IOException {
		final FileChannel channel = dataFiles.get(file.dataFile);
		final ByteBuffer blteDeclareHeader = ByteBuffer.allocate(8);

		final long blteOffset = file.fileOffset + FILE_ENTRY_HEADER_SIZE;
		long currentOffset = blteOffset;
		final long blteLimit = file.fileOffset + file.size;
		if (blteLimit - currentOffset < blteDeclareHeader.capacity()) {
			throw new MalformedCASCStructureException("BLTE header extends beyond file limits");
		}

		currentOffset += channel.read(blteDeclareHeader, currentOffset);
		if (blteDeclareHeader.hasRemaining()) {
			throw new EOFException("unexpected incomplete read");
		}
		blteDeclareHeader.flip();

		blteDeclareHeader.order(ByteOrder.BIG_ENDIAN);
		final byte[] mime = new byte[BLTE_MIME.length];
		blteDeclareHeader.get(mime);
		if (!Arrays.equals(mime, BLTE_MIME)) {
			throw new MalformedCASCStructureException("expected BLTE mime");
		}
		final long headerSize = Integer.toUnsignedLong(blteDeclareHeader.getInt());

		BLTEChunk[] chunks;
		if (headerSize > 0) {
			final long headerBodySize = headerSize - blteDeclareHeader.capacity();

			if (headerBodySize > Integer.MAX_VALUE) {
				throw new MalformedCASCStructureException("BLTE header too large to process");
			} else if (blteOffset + headerBodySize > blteLimit) {
				throw new MalformedCASCStructureException("BLTE header extends beyond file limits");
			}

			final ByteBuffer blteHeaderBody = ByteBuffer.allocate((int) headerBodySize);

			currentOffset += channel.read(blteHeaderBody, currentOffset);
			if (blteHeaderBody.hasRemaining()) {
				throw new EOFException("unexpected incomplete read");
			}
			blteHeaderBody.flip();

			blteHeaderBody.order(ByteOrder.BIG_ENDIAN);
			blteHeaderBody.mark();
			final byte flags = blteHeaderBody.get();
			if (flags != 0xF) {
				throw new MalformedCASCStructureException("unknown BLTE flags");
			}
			// BE24 read
			blteHeaderBody.reset();
			blteHeaderBody.put((byte) 0);
			blteHeaderBody.reset();

			final int chunkCount = blteHeaderBody.getInt();
			if (chunkCount < 0) {
				throw new MalformedCASCStructureException("BLTE chunk count too large to process");
			} else if (chunkCount == 0) {
				throw new MalformedCASCStructureException("invalid BLTE chunk count");
			}

			chunks = new BLTEChunk[chunkCount];
			long decompressedOffset = 0;
			long compressedOffset = currentOffset - file.fileOffset;
			for (int i = 0; i < chunks.length; i += 1) {
				final long compressedSize = Integer.toUnsignedLong(blteHeaderBody.getInt());
				final long decompressedSize = Integer.toUnsignedLong(blteHeaderBody.getInt());
				final byte[] checksumHash = new byte[16];
				blteHeaderBody.get(checksumHash);

				final BLTEChunk chunk = new BLTEChunk();
				chunk.compressedOffset = compressedOffset;
				chunk.compressedSize = compressedSize;
				chunk.decompressedOffset = decompressedOffset;
				chunk.decompressedSize = decompressedSize;
				chunk.checksumHash = checksumHash;
				chunks[i] = chunk;

				compressedOffset += compressedSize;
				decompressedOffset += decompressedSize;
			}
		} else {
			chunks = new BLTEChunk[1];
			final BLTEChunk chunk = new BLTEChunk();
			chunk.compressedOffset = currentOffset - file.fileOffset;
			chunk.compressedSize = blteLimit - currentOffset;
			chunk.decompressedOffset = 0;

			// unknown values, needs experimentation
			chunk.decompressedSize = chunk.compressedSize;
			chunk.checksumHash = null;
			chunks[0] = chunk;
		}
		return chunks;
	}

	public ByteBuffer getBLTEData(final FileEntry file, final BLTEChunk chunk, ByteBuffer blteDataBuffer)
			throws IOException {
		if (chunk.compressedSize + chunk.compressedOffset > file.size) {
			throw new MalformedCASCStructureException("BLTE data extends beyond file data");
		}

		if (blteDataBuffer == null || blteDataBuffer.remaining() < chunk.compressedSize) {
			blteDataBuffer = ByteBuffer.allocate((int) chunk.compressedSize);
		}

		final int blteDataBufferLimit = blteDataBuffer.limit();
		blteDataBuffer.limit(blteDataBuffer.position() + (int) chunk.compressedSize);
		try {
			final FileChannel channel = dataFiles.get(file.dataFile);
			channel.read(blteDataBuffer, file.fileOffset + chunk.compressedOffset);
			if (blteDataBuffer.hasRemaining()) {
				throw new EOFException("unexpected incomplete read");
			}
		} finally {
			blteDataBuffer.position(blteDataBuffer.limit());
			blteDataBuffer.limit(blteDataBufferLimit);
		}

		return blteDataBuffer;
	}

	public ByteBuffer getFileData(final BLTEChunk chunk, final ByteBuffer blteDataBuffer, ByteBuffer fileDataBuffer)
			throws IOException {
		if (blteDataBuffer.remaining() < chunk.compressedSize) {
			throw new MalformedCASCStructureException("BLTE data too small");
		}

		if (fileDataBuffer == null || fileDataBuffer.remaining() < chunk.decompressedSize) {
			fileDataBuffer = ByteBuffer.allocate((int) chunk.decompressedSize);
		}

		final int blteDataBufferLimit = blteDataBuffer.limit();
		final int fileDataBufferLimit = fileDataBuffer.limit();
		blteDataBuffer.limit(blteDataBuffer.position() + (int) chunk.compressedSize);
		fileDataBuffer.limit(fileDataBuffer.position() + (int) chunk.decompressedSize);
		try {
			final char encodingMode = (char) blteDataBuffer.get();
			switch (encodingMode) {
			case 'N':
				if (blteDataBuffer.remaining() != chunk.decompressedSize) {
					throw new MalformedCASCStructureException("not enough uncompressed bytes");
				}
				fileDataBuffer.put(blteDataBuffer);
				break;
			case 'Z':
				final Inflater zlib = new Inflater();
				zlib.setInput(blteDataBuffer.array(), blteDataBuffer.position(), blteDataBuffer.remaining());
				int resultSize;
				try {
					resultSize = zlib.inflate(fileDataBuffer.array(), fileDataBuffer.position(),
							fileDataBuffer.remaining());
				} catch (final DataFormatException e) {
					throw new MalformedCASCStructureException("zlib inflate exception", e);
				}
				if (resultSize != chunk.decompressedSize) {
					throw new MalformedCASCStructureException("not enough bytes generated: " + resultSize + "B");
				} else if (!zlib.finished()) {
					throw new MalformedCASCStructureException("unfinished inflate operation");
				}
				break;
			default:
				throw new UnsupportedEncodingException("unsupported encoding mode: " + encodingMode);
			}
		} finally {
			blteDataBuffer.position(blteDataBuffer.limit());
			blteDataBuffer.limit(blteDataBufferLimit);
			fileDataBuffer.position(fileDataBuffer.limit());
			fileDataBuffer.limit(fileDataBufferLimit);
		}

		return fileDataBuffer;
	}

	public static class FileEntry {
		private byte[] key;
		private int dataFile;
		private long fileOffset;
		private long size;
		private short flags;

		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append("FileEntry{key=0x");
			Hex.stringBufferAppendHex(builder, key);
			builder.append(", dataFile=");
			builder.append(dataFile);
			builder.append(", fileOffset=");
			builder.append(fileOffset);
			builder.append(", size=");
			builder.append(size);
			builder.append(", flags=");
			builder.append(Integer.toBinaryString(flags));
			builder.append("}");

			return builder.toString();
		}

		public boolean hasBLTE() {
			return size > FILE_ENTRY_HEADER_SIZE;
		}
	}

	public static class BLTEChunk {
		private long compressedOffset;
		private long compressedSize;
		private long decompressedOffset;
		private long decompressedSize;
		private byte[] checksumHash;

		public long getSize() {
			return decompressedSize;
		}

		public long getOffset() {
			return decompressedOffset;
		}

		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append("BLTEChunk{compressedSize=");
			builder.append(compressedSize);
			builder.append(", decompressedSize=");
			builder.append(decompressedSize);
			builder.append(", compressedOffset=");
			builder.append(compressedOffset);
			builder.append(", decompressedOffset=");
			builder.append(decompressedOffset);
			builder.append(", checksumHash=0x");
			Hex.stringBufferAppendHex(builder, checksumHash);
			builder.append("}");

			return builder.toString();
		}
	}
}
