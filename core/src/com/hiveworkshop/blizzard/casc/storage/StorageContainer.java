package com.hiveworkshop.blizzard.casc.storage;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.hiveworkshop.blizzard.casc.Key;
import com.hiveworkshop.blizzard.casc.nio.HashMismatchException;
import com.hiveworkshop.blizzard.casc.nio.MalformedCASCStructureException;

/**
 * High level storage container representing a storage entry.
 */
public class StorageContainer {
	/**
	 * Size of storage encoding key in bytes.
	 */
	private static final int ENCODING_KEY_SIZE = 16;

	/**
	 * Container encoding key.
	 */
	private Key key;
	private long size;
	private short flags;

	public StorageContainer(final ByteBuffer storageBuffer) throws IOException {
		final ByteBuffer containerBuffer = storageBuffer.slice();
		containerBuffer.order(ByteOrder.LITTLE_ENDIAN);

		// key is in reversed byte order
		final int checksumA;
		final int checksumB;
		try {
			final byte[] keyArray = new byte[ENCODING_KEY_SIZE];
			final int keyEnd = containerBuffer.position() + keyArray.length;
			for (int writeIndex = 0, readIndex = keyEnd
					- 1; writeIndex < keyArray.length; writeIndex += 1, readIndex -= 1) {
				keyArray[writeIndex] = containerBuffer.get(readIndex);
			}
			containerBuffer.position(keyEnd);

			key = new Key(keyArray);
			size = Integer.toUnsignedLong(containerBuffer.getInt());
			flags = containerBuffer.getShort();

			checksumA = containerBuffer.getInt();
			checksumB = containerBuffer.getInt();
		} catch (final BufferUnderflowException e) {
			throw new MalformedCASCStructureException("storage buffer too small");
		}

		final int computedA = checksumA; // TODO compute this
		final int computedB = checksumB; // TODO compute this
		if (checksumA != computedA) {
			throw new HashMismatchException("container checksum A mismatch");
		}
		if (checksumB != computedB) {
			throw new HashMismatchException("container checksum B mismatch");
		}

		storageBuffer.position(storageBuffer.position() + containerBuffer.position());
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("FileEntry{key=");
		builder.append(key);
		builder.append(", size=");
		builder.append(size);
		builder.append(", flags=");
		builder.append(Integer.toBinaryString(flags));
		builder.append("}");

		return builder.toString();
	}

	public long getSize() {
		return size;
	}

	public short getFlags() {
		return flags;
	}

	public Key getKey() {
		return key;
	}

}
