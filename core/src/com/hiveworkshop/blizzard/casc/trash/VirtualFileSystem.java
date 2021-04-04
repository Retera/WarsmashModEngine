package com.hiveworkshop.blizzard.casc.trash;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;

import com.hiveworkshop.blizzard.casc.nio.MalformedCASCStructureException;
//import com.hiveworkshop.blizzard.casc.vfs.path.Container;
//import com.hiveworkshop.blizzard.casc.vfs.path.FileFactory;
//import com.hiveworkshop.blizzard.casc.vfs.path.Node;

public class VirtualFileSystem {

	private static final ByteBuffer IDENTIFIER = ByteBuffer.wrap(new byte[] { 'T', 'V', 'F', 'S' });

	// private final ArrayList<Node> root;

	public VirtualFileSystem(final ByteBuffer fileBuffer) throws IOException {
		final ByteBuffer localBuffer = fileBuffer.slice();

		// check identifier

		if (localBuffer.remaining() < IDENTIFIER.remaining()
				|| !localBuffer.limit(IDENTIFIER.remaining()).equals(IDENTIFIER)) {
			throw new MalformedCASCStructureException("missing TVFS identifier");
		}

		// decode header

		localBuffer.limit(localBuffer.capacity());
		localBuffer.position(IDENTIFIER.remaining());

		final byte version = localBuffer.get();
		if (version != 1) {
			throw new UnsupportedOperationException("unsupported vfs version: " + version);
		}
		final int headerSize = Byte.toUnsignedInt(localBuffer.get());
		if (headerSize < 0x26) {
			throw new MalformedCASCStructureException("vfs header too small");
		}
		localBuffer.limit(headerSize);

		final int encodingKeySize = Byte.toUnsignedInt(localBuffer.get());
		final int patchKeySize = Byte.toUnsignedInt(localBuffer.get());

		final int flags = localBuffer.getInt();

		final int pathOffset = localBuffer.getInt();
		final int pathSize = localBuffer.getInt();
		final int fileReferenceOffset = localBuffer.getInt();
		final int fileReferenceSize = localBuffer.getInt();
		final int contentOffset = localBuffer.getInt();
		final int contentSize = localBuffer.getInt();

		final int maximumPathDepth = Short.toUnsignedInt(localBuffer.getShort());

		final int containerTableOffsetSize = Math.max(1,
				Integer.BYTES - Integer.numberOfLeadingZeros(contentSize) / Byte.SIZE);

		localBuffer.limit(pathOffset + pathSize);
		localBuffer.position(pathOffset);
		final ByteBuffer pathBuffer = localBuffer.slice();
		localBuffer.clear();

		localBuffer.limit(fileReferenceOffset + fileReferenceSize);
		localBuffer.position(fileReferenceOffset);
		final ByteBuffer fileReferenceBuffer = localBuffer.slice();
		localBuffer.clear();

		localBuffer.limit(contentOffset + contentSize);
		localBuffer.position(contentOffset);
		final ByteBuffer contentBuffer = localBuffer.slice();
		localBuffer.clear();

		// final var fileFactory = new FileFactory(fileReferenceBuffer, contentBuffer,
		// encodingKeySize, containerTableOffsetSize);

		// root = Container.decodeContainer(pathBuffer, fileFactory);
	}

	public void printPaths(final PrintStream out) {
		/*
		 * for (final var node : root) { node.printPaths(out, ""); }
		 */
	}
}
