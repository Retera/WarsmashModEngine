package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import java.util.ArrayList;
import java.util.List;

public class CRaceManager {
	private List<CRaceManagerEntry> raceEntries;
	private List<CRace> races;
	private List<CRacePreference> racePreferences;
	private CRacePreference randomRacePreference;

	public CRaceManager() {
		this.raceEntries = new ArrayList<>();
	}

	public void addRace(String key, int raceId, int racePrefId) {
		raceEntries.add(new CRaceManagerEntry(key, raceId, racePrefId));
	}

	public void build() {
		int maxRaceId = 0;
		int maxRacePrefId = 0;
		for (CRaceManagerEntry entry : raceEntries) {
			maxRaceId = Math.max(maxRaceId, entry.getRaceId());
			maxRacePrefId = Math.max(maxRacePrefId, entry.getRacePrefId());
		}
		this.races = new ArrayList<>(maxRaceId);
		int randomRacePrefId = maxRacePrefId + 1;
		this.racePreferences = new ArrayList<>(randomRacePrefId);
		for (int i = 0; i <= maxRaceId; i++) {
			races.add(null);
		}
		for (int i = 0; i <= maxRacePrefId; i++) {
			racePreferences.add(null);
		}
		for (CRaceManagerEntry entry : raceEntries) {
			int raceId = entry.getRaceId();
			int racePrefId = entry.getRacePrefId();
			races.set(raceId, new CRace(raceId));
			racePreferences.set(racePrefId, new CRacePreference(racePrefId));
		}
		randomRacePreference = new CRacePreference(randomRacePrefId);
		racePreferences.add(randomRacePreference);
	}

	public CRaceManagerEntry get(CRace race) {
		for (CRaceManagerEntry entry : raceEntries) {
			if (entry.getRaceId() == race.getId()) {
				return entry;
			}
		}
		return null;
	}

	public CRaceManagerEntry tryGet(CRace race) {
		CRaceManagerEntry entry = get(race);
		if (entry != null) {
			return entry;
		}
		return get(0);
	}

	public CRaceManagerEntry get(CRacePreference racePref) {
		int racePrefId = racePref.ordinal() + 1;
		for (CRaceManagerEntry entry : raceEntries) {
			if (entry.getRacePrefId() == racePrefId) {
				return entry;
			}
		}
		return null;
	}

	public CRaceManagerEntry tryGet(CRacePreference racePref) {
		CRaceManagerEntry entry = get(racePref);
		if (entry != null) {
			return entry;
		}
		return get(0);
	}

	public CRaceManagerEntry get(int index) {
		return raceEntries.get(index);
	}

	public int getEntryCount() {
		return raceEntries.size();
	}

	public CRace getRace(int id) {
		if ((id >= 0) && (id < races.size())) {
			return races.get(id);
		}
		return null;
	}

	public CRacePreference getRacePreferenceForRace(CRace race) {
		return getRacePreferenceById(get(race).getRacePrefId());
	}

	public CRacePreference getRacePreference(int bitMaskValue) {
		// TODO: we could do better performance than this in the future, maybe
		for (CRacePreference racePreference : racePreferences) {
			if (racePreference != null) {
				if (racePreference.getBitMaskValue() == bitMaskValue) {
					return racePreference;
				}
			}
		}
		return null;
	}

	public CRacePreference getRacePreferenceById(int id) {
		if ((id >= 0) && (id < racePreferences.size())) {
			return racePreferences.get(id);
		}
		return null;
	}

	public CRacePreference getRandomRacePreference() {
		return randomRacePreference;
	}
}
