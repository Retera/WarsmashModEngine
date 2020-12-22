package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CPlayerStateListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CPlayerStateListener.CPlayerStateNotifier;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;

public class CPlayer {
	private final int id;
	private int colorIndex;
	private final CMapControl controlType;
	private String name;
	private final CRace race;
	private final float[] startLocation;
	private final EnumSet<CRacePreference> racePrefs;
	private int gold = 500;
	private int lumber = 150;
	private int foodCap;
	private int foodUsed;
	private final EnumSet<CAllianceType>[] alliances = new EnumSet[WarsmashConstants.MAX_PLAYERS];
	private final Map<War3ID, Integer> rawcodeToTechtreeUnlocked = new HashMap<>();

	// if you use triggers for this then the transient tag here becomes really
	// questionable -- it already was -- but I meant for those to inform us
	// which fields shouldn't be persisted if we do game state save later
	private transient CPlayerStateNotifier stateNotifier = new CPlayerStateNotifier();

	public CPlayer(final int id, final CMapControl controlType, final String name, final CRace race,
			final float[] startLocation) {
		this.id = id;
		this.colorIndex = id;
		this.controlType = controlType;
		this.name = name;
		this.race = race;
		this.startLocation = startLocation;
		this.racePrefs = EnumSet.noneOf(CRacePreference.class);
		for (int i = 0; i < this.alliances.length; i++) {
			if (i == this.id) {
				// player is fully allied with self
				this.alliances[i] = EnumSet.allOf(CAllianceType.class);
			}
			else {
				this.alliances[i] = EnumSet.noneOf(CAllianceType.class);
			}
		}
	}

	public int getId() {
		return this.id;
	}

	public int getColorIndex() {
		return this.colorIndex;
	}

	public CMapControl getControlType() {
		return this.controlType;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public CRace getRace() {
		return this.race;
	}

	public boolean isRacePrefSet(final CRacePreference racePref) {
		return this.racePrefs.contains(racePref);
	}

	public void setAlliance(final CPlayer otherPlayer, final CAllianceType allianceType, final boolean value) {
		final EnumSet<CAllianceType> alliancesWithOtherPlayer = this.alliances[otherPlayer.getId()];
		if (value) {
			alliancesWithOtherPlayer.add(allianceType);
		}
		else {
			alliancesWithOtherPlayer.remove(allianceType);
		}
	}

	public boolean hasAlliance(final CPlayer otherPlayer, final CAllianceType allianceType) {
		return hasAlliance(otherPlayer.getId(), allianceType);
	}

	public boolean hasAlliance(final int otherPlayerIndex, final CAllianceType allianceType) {
		final EnumSet<CAllianceType> alliancesWithOtherPlayer = this.alliances[otherPlayerIndex];
		return alliancesWithOtherPlayer.contains(allianceType);
	}

	public int getGold() {
		return this.gold;
	}

	public int getLumber() {
		return this.lumber;
	}

	public int getFoodCap() {
		return this.foodCap;
	}

	public int getFoodUsed() {
		return this.foodUsed;
	}

	public float[] getStartLocation() {
		return this.startLocation;
	}

	public void setGold(final int gold) {
		this.gold = gold;
		this.stateNotifier.goldChanged();
	}

	public void setLumber(final int lumber) {
		this.lumber = lumber;
		this.stateNotifier.lumberChanged();
	}

	public void setFoodCap(final int foodCap) {
		this.foodCap = foodCap;
		this.stateNotifier.foodChanged();
	}

	public void setFoodUsed(final int foodUsed) {
		this.foodUsed = foodUsed;
		this.stateNotifier.foodChanged();
	}

	public void setColorIndex(final int colorIndex) {
		this.colorIndex = colorIndex;
	}

	public int getTechtreeUnlocked(final War3ID rawcode) {
		final Integer techtreeUnlocked = this.rawcodeToTechtreeUnlocked.get(rawcode);
		if (techtreeUnlocked == null) {
			return 0;
		}
		return techtreeUnlocked;
	}

	public void addStateListener(final CPlayerStateListener listener) {
		this.stateNotifier.subscribe(listener);
	}

	public void removeStateListener(final CPlayerStateListener listener) {
		this.stateNotifier.unsubscribe(listener);
	}

	public void chargeFor(final CUnitType unitType) {
		this.lumber -= unitType.getLumberCost();
		this.gold -= unitType.getGoldCost();
		this.stateNotifier.lumberChanged();
		this.stateNotifier.goldChanged();
	}

	public void refundFor(final CUnitType unitType) {
		this.lumber += unitType.getLumberCost();
		this.gold += unitType.getGoldCost();
		this.stateNotifier.lumberChanged();
		this.stateNotifier.goldChanged();
	}

	public void setUnitFoodUsed(final CUnit unit, final int foodUsed) {
		this.foodUsed += unit.setFoodUsed(foodUsed);
		this.stateNotifier.foodChanged();
	}

	public void setUnitFoodMade(final CUnit unit, final int foodMade) {
		this.foodCap += unit.setFoodMade(foodMade);
		this.stateNotifier.foodChanged();
	}
}
