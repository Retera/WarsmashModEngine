package com.etheller.warsmash.viewer5.handlers.w3x.simulation.config;

import java.util.EnumMap;
import java.util.EnumSet;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.ai.AIDifficulty;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CMapControl;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerJass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerState;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRacePreference;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRacePreferences;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CPlayerSlotState;

public abstract class CBasePlayer implements CPlayerJass {
	private final int id;
	private String name;
	private int team;
	private int startLocationIndex;
	private int forcedStartLocationIndex = -1;
	private int color;
	private final CRacePreferences racePrefs;
	private final EnumSet<CAllianceType>[] alliances;
	private final EnumMap<CPlayerState, Integer>[] taxRates;
	private boolean onScoreScreen;
	private boolean raceSelectable;
	private CMapControl mapControl = CMapControl.NONE;
	private CPlayerSlotState slotState = CPlayerSlotState.EMPTY;
	private AIDifficulty aiDifficulty = null;

	public CBasePlayer(final CBasePlayer other) {
		this.id = other.id;
		this.name = other.name;
		this.team = other.team;
		this.startLocationIndex = other.startLocationIndex;
		this.forcedStartLocationIndex = other.forcedStartLocationIndex;
		this.color = other.color;
		this.racePrefs = other.racePrefs;
		this.alliances = other.alliances;
		this.taxRates = other.taxRates;
		this.onScoreScreen = other.onScoreScreen;
		this.raceSelectable = other.raceSelectable;
		this.mapControl = other.mapControl;
		this.slotState = other.slotState;
	}

	public CBasePlayer(final int id) {
		this.id = id;
		this.color = id;
		this.name = "null";
		this.alliances = new EnumSet[WarsmashConstants.MAX_PLAYERS];
		this.taxRates = new EnumMap[WarsmashConstants.MAX_PLAYERS];
		this.racePrefs = new CRacePreferences();
		for (int i = 0; i < this.alliances.length; i++) {
			if (i == id) {
				// player is fully allied with self
				this.alliances[i] = EnumSet.allOf(CAllianceType.class);
			}
			else {
				this.alliances[i] = EnumSet.noneOf(CAllianceType.class);
			}
			this.taxRates[i] = new EnumMap<>(CPlayerState.class);
		}
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setOnScoreScreen(final boolean onScoreScreen) {
		this.onScoreScreen = onScoreScreen;
	}

	public boolean isOnScoreScreen() {
		return this.onScoreScreen;
	}

	@Override
	public void setRaceSelectable(final boolean raceSelectable) {
		this.raceSelectable = raceSelectable;
	}

	@Override
	public void setTeam(final int team) {
		this.team = team;
	}

	@Override
	public int getTeam() {
		return this.team;
	}

	@Override
	public int getStartLocationIndex() {
		return this.startLocationIndex;
	}

	@Override
	public void setStartLocationIndex(final int startLocationIndex) {
		this.startLocationIndex = startLocationIndex;
	}

	@Override
	public void setColor(final int color) {
		this.color = color;
	}

	@Override
	public int getColor() {
		return this.color;
	}

	@Override
	public boolean isRacePrefSet(final CRacePreference racePref) {
		return this.racePrefs.contains(racePref);
	}

	@Override
	public void setRacePref(final CRacePreference racePref) {
		// TODO should this clear? there's a case to be made for keeping old value
		// perhaps
		this.racePrefs.clear();
		this.racePrefs.add(racePref);
	}

	@Override
	public void setAlliance(final int otherPlayerIndex, final CAllianceType allianceType, final boolean value) {
		final EnumSet<CAllianceType> alliancesWithOtherPlayer = this.alliances[otherPlayerIndex];
		if (value) {
			alliancesWithOtherPlayer.add(allianceType);
		}
		else {
			alliancesWithOtherPlayer.remove(allianceType);
		}
	}

	@Override
	public boolean hasAlliance(final int otherPlayerIndex, final CAllianceType allianceType) {
		final EnumSet<CAllianceType> alliancesWithOtherPlayer = this.alliances[otherPlayerIndex];
		return alliancesWithOtherPlayer.contains(allianceType);
	}

	@Override
	public void forceStartLocation(final int startLocIndex) {
		this.forcedStartLocationIndex = startLocIndex;
	}

	@Override
	public void setTaxRate(final int otherPlayerIndex, final CPlayerState whichResource, final int rate) {
		this.taxRates[otherPlayerIndex].put(whichResource, rate);
	}

	@Override
	public void setController(final CMapControl mapControl) {
		this.mapControl = mapControl;

	}

	@Override
	public boolean isRaceSelectable() {
		return this.raceSelectable;
	}

	@Override
	public CMapControl getController() {
		return this.mapControl;
	}

	@Override
	public CPlayerSlotState getSlotState() {
		return this.slotState;
	}

	@Override
	public void setSlotState(final CPlayerSlotState slotState) {
		this.slotState = slotState;
	}

	@Override
	public AIDifficulty getAIDifficulty() {
		return this.aiDifficulty;
	}

	@Override
	public void setAIDifficulty(final AIDifficulty aiDifficulty) {
		this.aiDifficulty = aiDifficulty;
	}

	@Override
	public int getTaxRate(final int otherPlayerIndex, final CPlayerState whichResource) {
		final Integer taxRate = this.taxRates[otherPlayerIndex].get(whichResource);
		if (taxRate == null) {
			return 0;
		}
		return taxRate;
	}
}
