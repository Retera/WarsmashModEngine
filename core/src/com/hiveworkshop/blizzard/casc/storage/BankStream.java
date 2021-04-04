package com.hiveworkshop.blizzard.casc.storage;

import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import com.hiveworkshop.blizzard.casc.Key;
import com.hiveworkshop.blizzard.casc.nio.MalformedCASCStructureException;

/**
 * Allows high level access to stored file data banks. These data banks can be
 * assembled using higher level logic into a continuous file.
 */
public class BankStream {
	private final StorageContainer container;
	private final BLTEContent[] content;
	private final ByteBuffer streamBuffer;
	private int bank = 0;
	private boolean hasBanks;

	/**
	 * Constructs a bank steam from the given buffer. An optional key can be used to
	 * verify the right file is being processed. If a key is provided it is assumed
	 * the remaining size of the buffer exactly matches the container size.
	 *
	 * @param storageBuffer Storage buffer, as specified by an index file.
	 * @param key           File encoding key to check contents with, or null if no
	 *                      such check is required.
	 * @throws IOException If an exception occurs during decoding of the
	 *                     storageBuffer.
	 */
	public BankStream(final ByteBuffer storageBuffer, final Key encodingKey) throws IOException {
		ByteBuffer streamBuffer = storageBuffer.slice();
		container = new StorageContainer(streamBuffer);
		if ((encodingKey != null) && !container.getKey().equals(encodingKey)) {
			throw new MalformedCASCStructureException("container encoding key mismatch");
		}

		final int storageSize = (int) container.getSize();
		final int storageSizeDiff = Integer.compare(streamBuffer.capacity(), storageSize);

		if (storageSizeDiff < 0) {
			throw new MalformedCASCStructureException("container buffer smaller than container");
		} else if ((encodingKey != null) && (storageSizeDiff != 0)) {
			throw new MalformedCASCStructureException("container buffer size mismatch");
		} else if (storageSizeDiff > 0) {
			// resize buffer to match file
			final int streamPos = streamBuffer.position();
			streamBuffer.limit(storageSize);
			streamBuffer.position(0);
			streamBuffer = streamBuffer.slice();
			streamBuffer.position(streamPos);
		}

		if (streamBuffer.hasRemaining()) {
			content = BLTEContent.decodeContent(streamBuffer);
			hasBanks = true;
		} else {
			content = null;
			hasBanks = false;
		}

		this.streamBuffer = streamBuffer;
		storageBuffer.position(storageBuffer.position() + streamBuffer.capacity());
	}

	/**
	 * Get the length of the next bank in bytes.
	 *
	 * @return Length of bank in bytes.
	 * @throws EOFException If there are no more banks in this stream.
	 */
	public long getNextBankLength() throws EOFException {
		if (!hasNextBank()) {
			throw new EOFException("no more banks to decode");
		}

		return content.length != 0 ? content[bank].getDecompressedSize() : streamBuffer.remaining();
	}

	/**
	 * Decode a bank from the stream. The bank buffer must be large enough to
	 * receive the bank data as specified by getNextBankLength. A null buffer will
	 * automatically allocate one large enough. The position of the bank buffer will
	 * be advanced as appropriate, potentially allowing for many banks to be fetched
	 * in sequence.
	 *
	 * @param bankBuffer Buffer to receive bank data.
	 * @return If null then a new suitable buffer, otherwise bankBuffer.
	 * @throws IOException  If something goes wrong during bank extraction.
	 * @throws EOFException If there are no more banks in this stream.
	 */
	public ByteBuffer getBank(ByteBuffer bankBuffer) throws IOException {
		if (!hasNextBank()) {
			throw new EOFException("no more banks to decode");
		}

		if (content.length != 0) {
			final BLTEContent blteEntry = content[bank];
			final long encodedSize = blteEntry.getCompressedSize();
			final long decodedSize = blteEntry.getDecompressedSize();

			if (streamBuffer.remaining() < encodedSize) {
				throw new MalformedCASCStructureException("encoded data beyond end of file");
			} else if (bankBuffer == null) {
				if (decodedSize > Integer.MAX_VALUE) {
					throw new MalformedCASCStructureException("bank too large for Java to manipulate");
				}
				bankBuffer = ByteBuffer.allocate((int) decodedSize);
			} else if (bankBuffer.remaining() < decodedSize) {
				throw new BufferOverflowException();
			}

			final ByteBuffer encodedBuffer = ((ByteBuffer) streamBuffer.slice().limit((int) encodedSize)).slice();
			final ByteBuffer decodedBuffer = ((ByteBuffer) bankBuffer.slice().limit((int) decodedSize)).slice();
			final byte[] intermediateEncodedCopy = new byte[encodedBuffer.remaining()];
			final byte[] intermediateDecodedCopy = new byte[decodedBuffer.remaining()];

			final char encodingMode = (char) encodedBuffer.get();
			switch (encodingMode) {
			case 'N':
				// uncompressed data
				if (encodedBuffer.remaining() != decodedSize) {
					throw new MalformedCASCStructureException("not enough uncompressed bytes");
				}
				decodedBuffer.put(encodedBuffer);
				break;
			case 'Z':
				// zlib compressed data
				final Inflater zlib = new Inflater();
				encodedBuffer.get(intermediateEncodedCopy, 0, encodedBuffer.remaining());
				zlib.setInput(intermediateEncodedCopy);
				final int resultSize;
				try {
					resultSize = zlib.inflate(intermediateDecodedCopy);
					decodedBuffer.put(intermediateDecodedCopy, 0, resultSize);
				} catch (final DataFormatException e) {
					throw new MalformedCASCStructureException("zlib inflate exception", e);
				}
				if (resultSize != decodedSize) {
					throw new MalformedCASCStructureException("not enough bytes generated: " + resultSize + "B");
				} else if (!zlib.finished()) {
					throw new MalformedCASCStructureException("unfinished inflate operation");
				}
				break;
			default:
				throw new UnsupportedEncodingException("unsupported encoding mode: " + encodingMode);
			}

			streamBuffer.position(streamBuffer.position() + encodedBuffer.position());
			bankBuffer.position(bankBuffer.position() + decodedBuffer.position());

			bank += 1;
			if (bank == content.length) {
				hasBanks = false;
			}
		} else {
			// this logic is guessed and requires confirmation
			if (bankBuffer == null) {
				bankBuffer = ByteBuffer.allocate(streamBuffer.remaining());
			} else if (bankBuffer.remaining() < streamBuffer.remaining()) {
				throw new MalformedCASCStructureException("bank buffer too small");
			}

			bankBuffer.put(streamBuffer);
			hasBanks = false;
		}

		return bankBuffer;
	}

	/**
	 * Returns true while one or more banks are remaining to be streamed. Only valid
	 * if hasBanks returns true.
	 *
	 * @return True if another bank can be decoded, otherwise false.
	 */
	public boolean hasNextBank() {
		return hasBanks;
	}
}
