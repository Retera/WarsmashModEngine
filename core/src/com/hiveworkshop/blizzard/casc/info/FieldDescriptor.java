package com.hiveworkshop.blizzard.casc.info;

public class FieldDescriptor {
	private static final int NAME_TERMINATOR = '!';
	private static final int DATA_TYPE_TERMINATOR = ':';

	private final String name;
	private FieldDataType dataType;
	private final int size;

	/**
	 * Constructs a field descriptor from a field declaration string.
	 *
	 * @param encoded Field declaration string.
	 */
	public FieldDescriptor(final String encoded) {
		final int nameEnd = encoded.indexOf(NAME_TERMINATOR);
		if (nameEnd == -1) {
			throw new IllegalArgumentException("missing name terminator");
		}
		final int dataTypeEnd = encoded.indexOf(DATA_TYPE_TERMINATOR, nameEnd + 1);
		if (dataTypeEnd == -1) {
			throw new IllegalArgumentException("missing data type terminator");
		}

		name = encoded.substring(0, nameEnd);

		try {
			dataType = FieldDataType.valueOf(encoded.substring(nameEnd + 1, dataTypeEnd));
		} catch (final IllegalArgumentException e) {
			dataType = FieldDataType.UNSUPPORTED;
		}

		size = Integer.parseInt(encoded.substring(dataTypeEnd + 1));
	}

	/**
	 * Get the field name.
	 *
	 * @return Name of the field.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the field data type.
	 *
	 * @return Field data type.
	 */
	public FieldDataType getDataType() {
		return dataType;
	}

	/**
	 * Get the field size. Field size is the number of bytes required to represent
	 * the field in native form. A value of 0 means the field is variable length.
	 *
	 * @return Field size in bytes.
	 */
	public int getSize() {
		return size;
	}

}
