package com.etheller.warsmash.viewer5.handlers.w3x;

import java.util.HashMap;
import java.util.Map;

public class Variations {
	public static final Map<String, Integer> CLIFF_VARS;
	public static final Map<String, Integer> CITY_CLIFF_VARS;

	static {
		final Map<String, Integer> cliffVariations = new HashMap<>();
		cliffVariations.put("AAAB", 1);
		cliffVariations.put("AAAC", 1);
		cliffVariations.put("AABA", 1);
		cliffVariations.put("AABB", 2);
		cliffVariations.put("AABC", 0);
		cliffVariations.put("AACA", 1);
		cliffVariations.put("AACB", 0);
		cliffVariations.put("AACC", 1);
		cliffVariations.put("ABAA", 1);
		cliffVariations.put("ABAB", 1);
		cliffVariations.put("ABAC", 0);
		cliffVariations.put("ABBA", 2);
		cliffVariations.put("ABBB", 1);
		cliffVariations.put("ABBC", 0);
		cliffVariations.put("ABCA", 0);
		cliffVariations.put("ABCB", 0);
		cliffVariations.put("ABCC", 0);
		cliffVariations.put("ACAA", 1);
		cliffVariations.put("ACAB", 0);
		cliffVariations.put("ACAC", 1);
		cliffVariations.put("ACBA", 0);
		cliffVariations.put("ACBB", 0);
		cliffVariations.put("ACBC", 0);
		cliffVariations.put("ACCA", 1);
		cliffVariations.put("ACCB", 0);
		cliffVariations.put("ACCC", 1);
		cliffVariations.put("BAAA", 1);
		cliffVariations.put("BAAB", 1);
		cliffVariations.put("BAAC", 0);
		cliffVariations.put("BABA", 1);
		cliffVariations.put("BABB", 1);
		cliffVariations.put("BABC", 0);
		cliffVariations.put("BACA", 0);
		cliffVariations.put("BACB", 0);
		cliffVariations.put("BACC", 0);
		cliffVariations.put("BBAA", 1);
		cliffVariations.put("BBAB", 1);
		cliffVariations.put("BBAC", 0);
		cliffVariations.put("BBBA", 1);
		cliffVariations.put("BBCA", 0);
		cliffVariations.put("BCAA", 0);
		cliffVariations.put("BCAB", 0);
		cliffVariations.put("BCAC", 0);
		cliffVariations.put("BCBA", 0);
		cliffVariations.put("BCCA", 0);
		cliffVariations.put("CAAA", 1);
		cliffVariations.put("CAAB", 0);
		cliffVariations.put("CAAC", 1);
		cliffVariations.put("CABA", 0);
		cliffVariations.put("CABB", 0);
		cliffVariations.put("CABC", 0);
		cliffVariations.put("CACA", 1);
		cliffVariations.put("CACB", 0);
		cliffVariations.put("CACC", 1);
		cliffVariations.put("CBAA", 0);
		cliffVariations.put("CBAB", 0);
		cliffVariations.put("CBAC", 0);
		cliffVariations.put("CBBA", 0);
		cliffVariations.put("CBCA", 0);
		cliffVariations.put("CCAA", 1);
		cliffVariations.put("CCAB", 0);
		cliffVariations.put("CCAC", 1);
		cliffVariations.put("CCBA", 0);
		cliffVariations.put("CCCA", 1);
		CLIFF_VARS = cliffVariations;

		final Map<String, Integer> cityCliffVariations = new HashMap<>();
		cityCliffVariations.put("AAAB", 2);
		cityCliffVariations.put("AAAC", 1);
		cityCliffVariations.put("AABA", 1);
		cityCliffVariations.put("AABB", 3);
		cityCliffVariations.put("AABC", 0);
		cityCliffVariations.put("AACA", 1);
		cityCliffVariations.put("AACB", 0);
		cityCliffVariations.put("AACC", 3);
		cityCliffVariations.put("ABAA", 1);
		cityCliffVariations.put("ABAB", 2);
		cityCliffVariations.put("ABAC", 0);
		cityCliffVariations.put("ABBA", 3);
		cityCliffVariations.put("ABBB", 0);
		cityCliffVariations.put("ABBC", 0);
		cityCliffVariations.put("ABCA", 0);
		cityCliffVariations.put("ABCB", 0);
		cityCliffVariations.put("ABCC", 0);
		cityCliffVariations.put("ACAA", 1);
		cityCliffVariations.put("ACAB", 0);
		cityCliffVariations.put("ACAC", 2);
		cityCliffVariations.put("ACBA", 0);
		cityCliffVariations.put("ACBB", 0);
		cityCliffVariations.put("ACBC", 0);
		cityCliffVariations.put("ACCA", 3);
		cityCliffVariations.put("ACCB", 0);
		cityCliffVariations.put("ACCC", 1);
		cityCliffVariations.put("BAAA", 1);
		cityCliffVariations.put("BAAB", 3);
		cityCliffVariations.put("BAAC", 0);
		cityCliffVariations.put("BABA", 2);
		cityCliffVariations.put("BABB", 0);
		cityCliffVariations.put("BABC", 0);
		cityCliffVariations.put("BACA", 0);
		cityCliffVariations.put("BACB", 0);
		cityCliffVariations.put("BACC", 0);
		cityCliffVariations.put("BBAA", 3);
		cityCliffVariations.put("BBAB", 1);
		cityCliffVariations.put("BBAC", 0);
		cityCliffVariations.put("BBBA", 1);
		cityCliffVariations.put("BBCA", 0);
		cityCliffVariations.put("BCAA", 0);
		cityCliffVariations.put("BCAB", 0);
		cityCliffVariations.put("BCAC", 0);
		cityCliffVariations.put("BCBA", 0);
		cityCliffVariations.put("BCCA", 0);
		cityCliffVariations.put("CAAA", 1);
		cityCliffVariations.put("CAAB", 0);
		cityCliffVariations.put("CAAC", 3);
		cityCliffVariations.put("CABA", 0);
		cityCliffVariations.put("CABB", 0);
		cityCliffVariations.put("CABC", 0);
		cityCliffVariations.put("CACA", 2);
		cityCliffVariations.put("CACB", 0);
		cityCliffVariations.put("CACC", 1);
		cityCliffVariations.put("CBAA", 0);
		cityCliffVariations.put("CBAB", 0);
		cityCliffVariations.put("CBAC", 0);
		cityCliffVariations.put("CBBA", 0);
		cityCliffVariations.put("CBCA", 0);
		cityCliffVariations.put("CCAA", 3);
		cityCliffVariations.put("CCAB", 0);
		cityCliffVariations.put("CCAC", 1);
		cityCliffVariations.put("CCBA", 0);
		cityCliffVariations.put("CCCA", 1);
		CITY_CLIFF_VARS = cityCliffVariations;
	}

	public static int getCliffVariation(final String dir, final String tag, final int variation) {
		final Integer vars;
		if ("Cliffs".equals(dir)) {
			vars = CLIFF_VARS.get(tag);
		}
		else {
			vars = CITY_CLIFF_VARS.get(tag);
		}
		if (variation < vars) {
			return variation;
		}
		return variation % (vars + 1);
	}
}
