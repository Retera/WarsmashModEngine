package com.hiveworkshop.blizzard.casc;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.hiveworkshop.blizzard.casc.nio.MalformedCASCStructureException;
import com.hiveworkshop.nio.ByteBufferInputStream;

/**
 * File containing CASC configuration information. This is basically a
 * collection of keys with their assigned value. What the values mean depends on
 * the purpose of the key.
 */
public class ConfigurationFile {
	/**
	 * The name of the data folder containing the configuration files.
	 */
	public static final String CONFIGURATION_FOLDER_NAME = "config";

	/**
	 * Character encoding used by configuration files.
	 */
	public static final Charset FILE_ENCODING = Charset.forName("UTF8");

	/**
	 * Length of the configuration bucket folder names.
	 */
	public static final int BUCKET_NAME_LENGTH = 2;

	/**
	 * Number of configuration bucket folder tiers.
	 */
	public static final int BUCKET_TIERS = 2;

	/**
	 * Retrieve a configuration file from the data folder by its key.
	 *
	 * @param dataFolder Path of the CASC data folder.
	 * @param keyHex     Key for configuration file as a hexadecimal string.
	 * @return The requested configuration file.
	 * @throws IOException If an exception occurs when retrieving the file.
	 */
	public static ConfigurationFile lookupConfigurationFile(final Path dataFolder, final String keyHex)
			throws IOException {
		Path file = dataFolder.resolve(CONFIGURATION_FOLDER_NAME);
		for (int tier = 0; tier < BUCKET_TIERS; tier += 1) {
			final int keyOffset = tier * BUCKET_NAME_LENGTH;
			final String bucketFolderName = keyHex.substring(keyOffset, keyOffset + BUCKET_NAME_LENGTH);
			file = file.resolve(bucketFolderName);
		}

		file = file.resolve(keyHex);

		final ByteBuffer fileBuffer = ByteBuffer.wrap(Files.readAllBytes(file));

		return new ConfigurationFile(fileBuffer);
	}

	/**
	 * Underlying map holding the configuration data.
	 */
	private final Map<String, String> configuration = new HashMap<>();

	/**
	 * Construct a configuration file by decoding a file buffer.
	 *
	 * @param fileBuffer File buffer to decode from.
	 * @throws IOException If one or more IO errors occur.
	 */
	public ConfigurationFile(final ByteBuffer fileBuffer) throws IOException {
		try (final ByteBufferInputStream fileStream = new ByteBufferInputStream(fileBuffer);
				final Scanner lineScanner = new Scanner(new InputStreamReader(fileStream, FILE_ENCODING))) {
			while (lineScanner.hasNextLine()) {
				final String line = lineScanner.nextLine().trim();
				final int lineLength = line.indexOf('#');
				final String record;
				if (lineLength != -1) {
					record = line.substring(0, lineLength);
				} else {
					record = line;
				}

				if (!record.equals("")) {
					final int assignmentIndex = record.indexOf('=');
					if (assignmentIndex == -1) {
						throw new MalformedCASCStructureException(
								"configuration file line contains record with no assignment");
					}

					final String key = record.substring(0, assignmentIndex).trim();
					final String value = record.substring(assignmentIndex + 1).trim();

					if (configuration.putIfAbsent(key, value) != null) {
						throw new MalformedCASCStructureException(
								"configuration file contains duplicate key declarations");
					}
				}
			}
		}
	}

	/**
	 * Get the configuration defined by the file.
	 *
	 * @return Configuration map.
	 */
	public Map<String, String> getConfiguration() {
		return configuration;
	}

}
