package com.etheller.warsmash.util;

import java.io.IOException;

import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

public class ParseUtils {
	public static long parseUInt32(final LittleEndianDataInputStream stream) throws IOException {
		return stream.readInt() & 0xFFFFFFFF;
	}

	public static void readFloatArray(final LittleEndianDataInputStream stream, final float[] array)
			throws IOException {
		for (int i = 0; i < array.length; i++) {
			array[i] = stream.readFloat();
		}
	}

	public static void writeFloatArray(final LittleEndianDataOutputStream stream, final float[] array)
			throws IOException {
		for (int i = 0; i < array.length; i++) {
			stream.writeFloat(array[i]);
		}
	}
}
