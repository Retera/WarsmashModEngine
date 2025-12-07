package com.etheller.warsmash.util;

public class FlagUtils {

	public static boolean hasFlag(final int flagBitSet, final int whichFlag) {
		return (flagBitSet & whichFlag) != 0;
	}

}
