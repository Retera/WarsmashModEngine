package com.etheller.warsmash.viewer5.handlers.w3x.simulation.data;

import java.util.HashMap;
import java.util.Map;

public enum CUnitRace {
	HUMAN,
	ORC,
	UNDEAD,
	NIGHTELF,
	NAGA,
	CREEPS,
	DEMON,
	CRITTERS,
	OTHER;

	private static Map<String, CUnitRace> keyToRace = new HashMap<>();

	static {
		for (final CUnitRace race : CUnitRace.values()) {
			keyToRace.put(race.name(), race);
		}
	}

	public static CUnitRace parseRace(final String raceString) {
		final CUnitRace race = keyToRace.get(raceString.toUpperCase());
		if (race == null) {
			return OTHER;
		}
		return race;
	}
}