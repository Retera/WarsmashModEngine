package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

public class CRaceManagerEntry {
	private final String key;
	private final int raceId;
	private final int racePrefId;

	public CRaceManagerEntry(String key, int raceId, int racePrefId) {
		this.key = key;
		this.raceId = raceId;
		this.racePrefId = racePrefId;
	}

	public String getKey() {
		return key;
	}

	public int getRaceId() {
		return raceId;
	}

	public int getRacePrefId() {
		return racePrefId;
	}
}
