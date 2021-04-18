package com.hiveworkshop.blizzard.casc.vfs;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hiveworkshop.blizzard.casc.Key;
import com.hiveworkshop.blizzard.casc.nio.MalformedCASCStructureException;

/**
 * Decodes file data for file value nodes.
 * <p>
 * This is done by collating together data. First the file is resolved from the
 * file buffer then the data describing its contents is resolved from the
 * contents buffer.
 */
public class TVFSDecoder {
	/**
	 * TVFS file identifier located at start of TVFS files.
	 */
	private static final ByteBuffer IDENTIFIER = ByteBuffer.wrap(new byte[] { 'T', 'V', 'F', 'S' });

	/**
	 * Flag for container values. If set inside a value then the value is a
	 * container of other nodes otherwise it is a file.
	 */
	private static final int VALUE_CONTAINER_FLAG = 0x80000000;

	/**
	 * Specifier for path node value. If path string length is this then value
	 * follows.
	 */
	private static final int VALUE_PATH_STRING_LENGTH = 0xFF;

	private byte version = 0;
	private int flags = 0;
	private int encodingKeySize = 0;
	private int patchKeySize = 0;
	private int pathOffset = 0;
	private int pathSize = 0;
	private int fileReferenceOffset = 0;
	private int fileReferenceSize = 0;
	private int cascReferenceOffset = 0;
	private int cascReferenceSize = 0;
	private int maximumPathDepth = 0;

	private int contentsOffsetSize = 0;
	private ByteBuffer pathBuffer = null;
	private ByteBuffer logicalBuffer = null;
	private ByteBuffer storageBuffer = null;

	/**
	 * The offset into the content buffer is a special type that uses the minimum
	 * number of bytes to hold the largest offset. Hence non-standard types such as
	 * 3 bytes long big-endian integer are possible so a special buffer is needed to
	 * decode these numbers.
	 */
	private final ByteBuffer contentsOffsetDecoder;

	public TVFSDecoder() {
		contentsOffsetDecoder = ByteBuffer.allocate(Integer.BYTES);
	}

	public List<PathNode> decodeContainer() throws MalformedCASCStructureException {
		return decodeContainer(pathBuffer);
	}

	private List<PathNode> decodeContainer(final ByteBuffer pathBuffer) throws MalformedCASCStructureException {
		final ArrayList<PathNode> nodes = new ArrayList<PathNode>();

		while (pathBuffer.hasRemaining()) {
			final PathNode node = decodeNode(pathBuffer);
			nodes.add(node);
		}

		return nodes;
	}

	private PathNode decodeNode(final ByteBuffer pathBuffer) throws MalformedCASCStructureException {
		final ArrayList<byte[]> pathFragments = new ArrayList<byte[]>();

		PathNode node;
		try {
			int pathStringLength;
			while ((pathStringLength = Byte.toUnsignedInt(pathBuffer.get())) != VALUE_PATH_STRING_LENGTH) {
				final byte[] pathFragment = new byte[pathStringLength];
				pathBuffer.get(pathFragment);
				pathFragments.add(pathFragment);
			}

			final int value = pathBuffer.getInt();
			if ((value & VALUE_CONTAINER_FLAG) != 0) {
				// prefix node
				final int containerSize = value & ~VALUE_CONTAINER_FLAG;
				pathBuffer.position(pathBuffer.position() - Integer.BYTES);

				if (containerSize > pathBuffer.remaining()) {
					throw new MalformedCASCStructureException("prefix node container extends beyond path container");
				}

				pathBuffer.limit(pathBuffer.position() + containerSize);

				final ByteBuffer containerBuffer = pathBuffer.slice();

				pathBuffer.position(pathBuffer.limit());
				pathBuffer.limit(pathBuffer.capacity());

				containerBuffer.position(Integer.BYTES);

				final List<PathNode> nodes = decodeContainer(containerBuffer);

				node = new PrefixNode(pathFragments, nodes);
			} else {
				// file value
				final StorageReference[] fileReferences = getFileReferences(value);

				node = new FileNode(pathFragments, Arrays.asList(fileReferences));
			}
		} catch (final BufferUnderflowException e) {
			throw new MalformedCASCStructureException("path stream goes beyond path container");
		}

		return node;
	}

	private StorageReference[] getFileReferences(final int fileOffset) throws MalformedCASCStructureException {
		if (fileOffset > logicalBuffer.limit()) {
			throw new MalformedCASCStructureException("logical offset beyond file reference chunk");
		}
		logicalBuffer.position(fileOffset);

		StorageReference[] references;

		try {
			final int referenceCount = Byte.toUnsignedInt(logicalBuffer.get());
			references = new StorageReference[referenceCount];
			for (int i = 0; i < referenceCount; i += 1) {
				final long offset = Integer.toUnsignedLong(logicalBuffer.getInt());
				final long size = Integer.toUnsignedLong(logicalBuffer.getInt());

				logicalBuffer.get(contentsOffsetDecoder.array(), Integer.BYTES - contentsOffsetSize,
						contentsOffsetSize);
				final int cascReferenceOffset = contentsOffsetDecoder.getInt(0);

				if (cascReferenceOffset > storageBuffer.limit()) {
					throw new MalformedCASCStructureException("storage offset beyond casc reference chunk");
				}
				storageBuffer.position(cascReferenceOffset);

				try {
					final byte[] encodingKeyDecoder = new byte[encodingKeySize];
					storageBuffer.get(encodingKeyDecoder);

					final int physicalSize = storageBuffer.getInt();
					storageBuffer.get();
					final int actualSize = storageBuffer.getInt();

					final StorageReference reference = new StorageReference(offset, size, new Key(encodingKeyDecoder),
							physicalSize, actualSize);
					references[i] = reference;
				} catch (final BufferUnderflowException e) {
					throw new MalformedCASCStructureException("storage goes out of bounds");
				}
			}
		} catch (final BufferUnderflowException e) {
			throw new MalformedCASCStructureException("logical reference goes out of bounds");
		}

		return references;
	}

	public TVFSFile loadFile(final ByteBuffer fileBuffer) throws IOException {
		final ByteBuffer localBuffer = fileBuffer.slice();

		// check identifier

		if ((localBuffer.remaining() < IDENTIFIER.remaining())
				|| !localBuffer.limit(IDENTIFIER.remaining()).equals(IDENTIFIER)) {
			throw new MalformedCASCStructureException("missing TVFS identifier");
		}

		// decode header

		localBuffer.limit(localBuffer.capacity());
		localBuffer.position(IDENTIFIER.remaining());

		try {
			version = localBuffer.get();
			if (version != 1) {
				throw new UnsupportedOperationException("unsupported TVFS version: " + version);
			}
			final int headerSize = Byte.toUnsignedInt(localBuffer.get());
			if (headerSize > localBuffer.capacity()) {
				throw new MalformedCASCStructureException("TVFS header extends past end of file");
			}
			localBuffer.limit(headerSize);

			encodingKeySize = Byte.toUnsignedInt(localBuffer.get());
			patchKeySize = Byte.toUnsignedInt(localBuffer.get());
			flags = localBuffer.getInt();

			pathOffset = localBuffer.getInt();
			pathSize = localBuffer.getInt();
			if ((Integer.toUnsignedLong(pathOffset) + Integer.toUnsignedLong(pathSize)) > localBuffer.capacity()) {
				throw new MalformedCASCStructureException("path stream extends past end of file");
			}

			fileReferenceOffset = localBuffer.getInt();
			fileReferenceSize = localBuffer.getInt();
			if ((Integer.toUnsignedLong(fileReferenceOffset) + Integer.toUnsignedLong(fileReferenceSize)) > localBuffer
					.capacity()) {
				throw new MalformedCASCStructureException("logical data extends past end of file");
			}

			cascReferenceOffset = localBuffer.getInt();
			cascReferenceSize = localBuffer.getInt();
			if ((Integer.toUnsignedLong(cascReferenceOffset) + Integer.toUnsignedLong(cascReferenceSize)) > localBuffer
					.capacity()) {
				throw new MalformedCASCStructureException("storage data extends past end of file");
			}

			maximumPathDepth = Short.toUnsignedInt(localBuffer.getShort());
		} catch (final BufferUnderflowException e) {
			throw new MalformedCASCStructureException("header goes out of bounds");
		}

		contentsOffsetSize = Math.max(1, Integer.BYTES - (Integer.numberOfLeadingZeros(cascReferenceSize) / Byte.SIZE));
		contentsOffsetDecoder.putInt(0, 0);

		localBuffer.limit(pathOffset + pathSize);
		localBuffer.position(pathOffset);
		pathBuffer = localBuffer.slice();
		localBuffer.clear();

		localBuffer.limit(fileReferenceOffset + fileReferenceSize);
		localBuffer.position(fileReferenceOffset);
		logicalBuffer = localBuffer.slice();
		localBuffer.clear();

		localBuffer.limit(cascReferenceOffset + cascReferenceSize);
		localBuffer.position(cascReferenceOffset);
		storageBuffer = localBuffer.slice();
		localBuffer.clear();

		final List<PathNode> rootNodes = decodeContainer();
		final TVFSFile tvfsFile = new TVFSFile(version, flags, encodingKeySize, patchKeySize, maximumPathDepth,
				rootNodes);

		return tvfsFile;
	}
}
