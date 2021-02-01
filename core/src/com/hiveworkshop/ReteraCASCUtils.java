package com.hiveworkshop;

public class ReteraCASCUtils {

	public static boolean arraysEquals(final byte[] a, final int aFromIndex, final int aToIndex, final byte[] b,
			final int bFromIndex, final int bToIndex) {
		if (a == null) {
			if (b == null) {
				return true;
			} else {
				return false;
			}
		}
		if (b == null) {
			return false;
		}
		if ((aToIndex - aFromIndex) != (bToIndex - bFromIndex)) {
			return false;
		}
		int j = bFromIndex;
		for (int i = aFromIndex; i < aToIndex; i++) {
			if (a[i] != b[j++]) {
				return false;
			}
		}
		return true;
	}

	public static int arraysCompareUnsigned(final byte[] a, final int aFromIndex, final int aToIndex, final byte[] b,
			final int bFromIndex, final int bToIndex) {
		final int i = arraysMismatch(a, aFromIndex, aToIndex, b, bFromIndex, bToIndex);
		if ((i >= 0) && (i < Math.min(aToIndex - aFromIndex, bToIndex - bFromIndex))) {
			return byteCompareUnsigned(a[aFromIndex + i], b[bFromIndex + i]);
		}
		return (aToIndex - aFromIndex) - (bToIndex - bFromIndex);
	}

	private static int byteCompareUnsigned(final byte b, final byte c) {
		return Integer.compare(b & 0xFF, c & 0xFF);
	}

	private static int arraysMismatch(final byte[] a, final int aFromIndex, final int aToIndex, final byte[] b,
			final int bFromIndex, final int bToIndex) {
		final int aLength = aToIndex - aFromIndex;
		final int bLength = bToIndex - bFromIndex;
		for (int i = 0; (i < aLength) && (i < bLength); i++) {
			if (a[aFromIndex + i] != b[bFromIndex + i]) {
				return i;
			}
		}
		return Math.min(aLength, bLength);
	}
}
