package com.etheller.warsmash.util;

public final class War3ID implements Comparable<War3ID> {
	public static final War3ID NONE = new War3ID(0);
	private final int value;

	public War3ID(final int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	public static War3ID fromString(String idString) {
		if (idString.length() == 3) {
			System.err.println(
					"Loaded custom data for the ability CURSE whose MetaData field, 'Crs', is the only 3 letter War3ID in the game. This might cause unexpected errors, so watch your % chance to miss in custom curse abilities carefully.");
			idString += '\0';
		}
		if (idString.length() != 4) {
			throw new IllegalArgumentException(
					"A War3ID must be 4 ascii characters in length (got " + idString.length() + ") '" + idString + "'");
		}
		return new War3ID(RawcodeUtils.toInt(idString));
	}

	public String asStringValue() {
		String string = RawcodeUtils.toString(this.value);
		if (((string.charAt(3) == '\0') || (string.charAt(3) == ' ')) && (string.charAt(2) != '\0')) {
			string = string.substring(0, 3);
		}
		return string;
	}

	public War3ID set(final int index, final char c) {
		final String asStringValue = asStringValue();
		String result = asStringValue.substring(0, index);
		result += c;
		result += asStringValue.substring(index + 1, asStringValue.length());
		return War3ID.fromString(result);
	}

	public char charAt(final int index) {
		return (char) ((this.value >>> ((3 - index) * 8)) & 0xFF);
	}

	@Override
	public String toString() {
		return asStringValue();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + this.value;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final War3ID other = (War3ID) obj;
		if (this.value != other.value) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(final War3ID o) {
		return Integer.compare(this.value, o.value);
	}
}
