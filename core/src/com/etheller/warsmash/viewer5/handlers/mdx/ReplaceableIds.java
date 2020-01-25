package com.etheller.warsmash.viewer5.handlers.mdx;

import java.util.HashMap;
import java.util.Map;

import com.etheller.warsmash.util.WarsmashConstants;

public class ReplaceableIds {
	private static final Map<Long, String> ID_TO_STR = new HashMap<>();
	private static final Map<Long, String> REPLACEABLE_ID_TO_STR = new HashMap<>();

	static {
		for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
			ID_TO_STR.put(Long.valueOf(i), String.format("%2d", i).replace(' ', '0'));
		}
		REPLACEABLE_ID_TO_STR.put(Long.valueOf(1), "TeamColor\\TeamColor");
		REPLACEABLE_ID_TO_STR.put(Long.valueOf(2), "TeamGlow\\TeamGlow");
		REPLACEABLE_ID_TO_STR.put(Long.valueOf(11), "Cliff\\Cliff0");
		REPLACEABLE_ID_TO_STR.put(Long.valueOf(21), ""); // Used by all cursor models (HumanCursor, OrcCursor,
															// UndeadCursor, NightElfCursor)
		REPLACEABLE_ID_TO_STR.put(Long.valueOf(31), "LordaeronTree\\LordaeronSummerTree");
		REPLACEABLE_ID_TO_STR.put(Long.valueOf(32), "AshenvaleTree\\AshenTree");
		REPLACEABLE_ID_TO_STR.put(Long.valueOf(33), "BarrensTree\\BarrensTree");
		REPLACEABLE_ID_TO_STR.put(Long.valueOf(34), "NorthrendTree\\NorthTree");
		REPLACEABLE_ID_TO_STR.put(Long.valueOf(35), "Mushroom\\MushroomTree");
		REPLACEABLE_ID_TO_STR.put(Long.valueOf(36), "RuinsTree\\RuinsTree");
		REPLACEABLE_ID_TO_STR.put(Long.valueOf(37), "OutlandMushroomTree\\MushroomTree");
	}

	public static void main(final String[] args) {
		System.out.println(ID_TO_STR);
	}

	public static String getIdString(final long replaceableId) {
		return ID_TO_STR.get(replaceableId);
	}

	public static String getPathString(final long replaceableId) {
		return REPLACEABLE_ID_TO_STR.get(replaceableId);
	}
}
