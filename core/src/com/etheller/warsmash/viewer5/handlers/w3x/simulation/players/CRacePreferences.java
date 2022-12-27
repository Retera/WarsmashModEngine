package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

public class CRacePreferences {
	private int bitFlags;

	public void add(CRacePreference racePreference) {
		bitFlags |= racePreference.getBitMaskValue();
	}

	public boolean contains(CRacePreference racePreference) {
		return (bitFlags & racePreference.getBitMaskValue()) != 0;
	}

	public void clear() {
		bitFlags = 0;
	}
}
