package com.hiveworkshop.blizzard.casc.info;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.hiveworkshop.blizzard.casc.nio.MalformedCASCStructureException;
import com.hiveworkshop.nio.ByteBufferInputStream;

/**
 * Top level CASC information file containing configuration information and
 * entry point references.
 */
public class Info {
	/**
	 * Name of the CASC build info file located in the install root (parent of the
	 * data folder).
	 */
	public static final String BUILD_INFO_FILE_NAME = ".build.info";

	/**
	 * Character encoding used by info files.
	 */
	public static final Charset FILE_ENCODING = Charset.forName("UTF8");

	/**
	 * Field separator used by CASC info files.
	 */
	private static final String FIELD_SEPARATOR_REGEX = "\\|";

	/**
	 * Helper method to separate a single line of info file into separate field
	 * strings.
	 *
	 * @param encodedLine Line of info file.
	 * @return Array of separate fields.
	 */
	private static String[] separateFields(final String encodedLine) {
		return encodedLine.split(FIELD_SEPARATOR_REGEX);
	}

	private final ArrayList<FieldDescriptor> fieldDescriptors = new ArrayList<>();

	private final ArrayList<ArrayList<String>> records = new ArrayList<>();

	/**
	 * Construct an info file from an array of encoded lines.
	 *
	 * @param encodedLines Encoded lines.
	 * @throws IOException
	 */
	public Info(final ByteBuffer fileBuffer) throws IOException {
		try (final ByteBufferInputStream fileStream = new ByteBufferInputStream(fileBuffer);
				final Scanner lineScanner = new Scanner(new InputStreamReader(fileStream, FILE_ENCODING))) {
			final String[] encodedFieldDescriptors = separateFields(lineScanner.nextLine());
			for (final String encodedFieldDescriptor : encodedFieldDescriptors) {
				fieldDescriptors.add(new FieldDescriptor(encodedFieldDescriptor));
			}

			while (lineScanner.hasNextLine()) {
				records.add(new ArrayList<>(Arrays.asList(separateFields(lineScanner.nextLine()))));
			}
		} catch (final NoSuchElementException e) {
			throw new MalformedCASCStructureException("missing headers");
		}
	}

	/**
	 * Retrieves a specific field of a record.
	 *
	 * @param recordIndex Record index to lookup.
	 * @param fieldIndex  Field index to retrieve of record.
	 * @return Field value.
	 * @throws IndexOutOfBoundsException When recordIndex or fieldIndex are out of
	 *                                   bounds.
	 */
	public String getField(final int recordIndex, final int fieldIndex) {
		return records.get(recordIndex).get(fieldIndex);
	}

	/**
	 * Retrieves a specific field of a record.
	 *
	 * @param recordIndex Record index to lookup.
	 * @param fieldName   Field name to retrieve of record.
	 * @return Field value, or null if field does not exist.
	 * @throws IndexOutOfBoundsException When recordIndex is out of bounds.
	 */
	public String getField(final int recordIndex, final String fieldName) {
		// resolve field
		final int fieldIndex = getFieldIndex(fieldName);
		if (fieldIndex == -1) {
			// field does not exist
			return null;
		}

		return getField(recordIndex, fieldIndex);
	}

	/**
	 * Get the number of fields that make up each record.
	 *
	 * @return Field count.
	 */
	public int getFieldCount() {
		return fieldDescriptors.size();
	}

	/**
	 * Retrieve the field descriptor of a field index.
	 *
	 * @param fieldIndex Field index to retrieve descriptor from.
	 * @return Field descriptor for field index.
	 */
	public FieldDescriptor getFieldDescriptor(final int fieldIndex) {
		return fieldDescriptors.get(fieldIndex);
	}

	/**
	 * Lookup the index of a named field. Returns the field index for the field name
	 * if found, otherwise returns -1.
	 *
	 * @param name Name of the field to find.
	 * @return Field index of field.
	 */
	public int getFieldIndex(final String name) {
		for (int i = 0; i < fieldDescriptors.size(); i += 1) {
			if (fieldDescriptors.get(i).getName().equals(name)) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Get the number of records in this file.
	 *
	 * @return Record count.
	 */
	public int getRecordCount() {
		return records.size();
	}
}
