package com.hiveworkshop.lang;

import java.util.Arrays;

public abstract class Hex {
	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F' };

	public static final int RADIX_SIZE = 4;

	public static final int RADIX = HEX_DIGITS.length;

	private static final byte NO_VALUE = -1;

	private static final int DECIMAL_CHARACTERS = 10;

	private static final byte[] CHAR_VALUES = new byte[1 << Byte.SIZE];
	static {
		Arrays.fill(CHAR_VALUES, NO_VALUE);
		int value = 0;
		for (; value < DECIMAL_CHARACTERS; value += 1) {
			CHAR_VALUES['0' + value] = (byte) value;
		}
		for (; value < RADIX; value += 1) {
			CHAR_VALUES[('a' - DECIMAL_CHARACTERS) + value] = (byte) value;
			CHAR_VALUES[('A' - DECIMAL_CHARACTERS) + value] = (byte) value;
		}
	}

	/**
	 * Hexadecimal prefix string.
	 */
	public static final String HEX_PREFIX = "0x";

	private static int NIBBLE_MASK = 0b1111;

	public static byte decodeNibble(final int codePoint) {
		if (codePoint > CHAR_VALUES.length) {
			return NO_VALUE;
		} else {
			return CHAR_VALUES[codePoint];
		}
	}

	public static byte[] decodeHex(final CharSequence hex) {
		final int nibbleCount = hex.length();
		int valueNibbleShift = ((nibbleCount - 1) % (Byte.SIZE / RADIX_SIZE)) * RADIX_SIZE;
		final byte[] values = new byte[(nibbleCount + 1) >> 1];
		int valueIndex = 0;
		int value = 0;

		for (int nibbleIndex = 0; nibbleIndex < nibbleCount; nibbleIndex += 1) {
			final byte nibble = decodeNibble(hex.charAt(nibbleIndex));
			if (nibble == NO_VALUE) {
				throw new NumberFormatException("non-hex character");
			}

			value |= nibble << valueNibbleShift;

			if (valueNibbleShift == 0) {
				valueNibbleShift = Byte.SIZE;
				values[valueIndex++] = (byte) value;
				value = 0;
			}

			valueNibbleShift -= RADIX_SIZE;
		}

		return values;
	}

	public static void stringBufferAppendHex(final StringBuilder builder, final byte hex) {
		builder.append(HEX_DIGITS[(hex >> 4) & NIBBLE_MASK]);
		builder.append(HEX_DIGITS[hex & NIBBLE_MASK]);
	}

	public static void stringBufferAppendHex(final StringBuilder builder, final byte[] hex) {
		for (int i = 0; i < hex.length; i += 1) {
			stringBufferAppendHex(builder, hex[i]);
		}
	}
}
