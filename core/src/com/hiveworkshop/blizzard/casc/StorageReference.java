package com.hiveworkshop.blizzard.casc;

import java.util.Map;

/**
 * A reference to a file extracted from a configuration file.
 */
public class StorageReference {
	/**
	 * Suffix for sizes mapping entry in configuration files.
	 */
	private static final String SIZES_SUFFIX = "-size";

	private final long storedSize;
	private final long size;
	private final Key encodingKey;
	private final Key contentKey;

	/**
	 * Decodes a storage reference from a configuration file.
	 *
	 * @param name          Name of reference.
	 * @param configuration Map of configuration file content.
	 */
	public StorageReference(final String name, final Map<String, String> configuration) {
		final String keys = configuration.get(name);
		if (keys == null) {
			throw new IllegalArgumentException("name does not exist in configuration");
		}
		final String sizes = configuration.get(name + SIZES_SUFFIX);
		if (sizes == null) {
			throw new IllegalArgumentException("size missing in configuration");
		}

		final String[] keyStrings = keys.split(" ");
		contentKey = new Key(keyStrings[0]);
		encodingKey = new Key(keyStrings[1]);

		final String[] sizeStrings = sizes.split(" ");
		size = Long.parseLong(sizeStrings[0]);
		storedSize = Long.parseLong(sizeStrings[1]);
	}

	/**
	 * Content key?
	 *
	 * @return Content key.
	 */
	public Key getContentKey() {
		return contentKey;
	}

	/**
	 * Encoding key used to lookup the file from CASC storage.
	 *
	 * @return Encoding key.
	 */
	public Key getEncodingKey() {
		return encodingKey;
	}

	/**
	 * File size.
	 *
	 * @return File size in bytes of the file.
	 */
	public long getSize() {
		return size;
	}

	/**
	 * Size of file content in CASC storage.
	 *
	 * @return Approximate byte usage of file in CASC storage.
	 */
	public long getStoredSize() {
		return storedSize;
	}

}
