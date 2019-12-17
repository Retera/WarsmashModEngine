package com.etheller.warsmash.viewer5.handlers.mdx;

import java.util.HashMap;
import java.util.Map;

public class ReplaceableIds {
	private static final Map<Long, String> ID_TO_STR = new HashMap<>();

	static {
		for (int i = 0; i < 28; i++) {
			ID_TO_STR.put(Long.valueOf(i), String.format("%2d", i).replace(' ', '0'));
		}
	}

	public static void main(final String[] args) {
		System.out.println(ID_TO_STR);
	}

	public static String get(final long replaceableId) {
		return ID_TO_STR.get(replaceableId);
	}
}
