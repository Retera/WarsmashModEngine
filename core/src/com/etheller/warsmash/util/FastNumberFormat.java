package com.etheller.warsmash.util;

public class FastNumberFormat {
	private static final StringBuilder RECYCLE_STRING_BUILDER = new StringBuilder();

	public static String formatWholeNumber(final float value) {
		int intValue = (int) value;
		RECYCLE_STRING_BUILDER.setLength(0);
		do {
			RECYCLE_STRING_BUILDER.append(intValue % 10);
			intValue /= 10;
		}
		while (intValue > 0);
		final int len = RECYCLE_STRING_BUILDER.length();
		final int halfLength = len / 2;
		for (int i = 0; i < halfLength; i++) {
			final char swapCharA = RECYCLE_STRING_BUILDER.charAt(i);
			final char swapCharB = RECYCLE_STRING_BUILDER.charAt(len - 1 - i);
			RECYCLE_STRING_BUILDER.setCharAt(len - 1 - i, swapCharA);
			RECYCLE_STRING_BUILDER.setCharAt(i, swapCharB);
		}
		return RECYCLE_STRING_BUILDER.toString();
	}
}
