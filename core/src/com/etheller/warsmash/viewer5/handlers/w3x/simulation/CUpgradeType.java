package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CUpgradeClass;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CUnitRace;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.upgrade.CUpgradeEffect;

public class CUpgradeType {
	private War3ID typeId;
	private List<CUpgradeEffect> upgradeEffects;
	private boolean appliesToAllUnits;
	private CUpgradeClass upgradeClass;
	private int goldBase;
	private int goldIncrement;
	private int levelCount;
	private int lumberBase;
	private int lumberIncrement;
	private CUnitRace unitRace;
	private int timeBase;
	private int timeIncrement;
	private boolean transferWithUnitOwnership;
	private final List<UpgradeLevel> levelData;

	public CUpgradeType(War3ID typeId, List<CUpgradeEffect> upgradeEffects, boolean appliesToAllUnits,
			CUpgradeClass upgradeClass, int goldBase, int goldIncrement, int levelCount, int lumberBase,
			int lumberIncrement, CUnitRace unitRace, int timeBase, int timeIncrement, boolean transferWithUnitOwnership,
			List<UpgradeLevel> levelData) {
		this.typeId = typeId;
		this.upgradeEffects = upgradeEffects;
		this.appliesToAllUnits = appliesToAllUnits;
		this.upgradeClass = upgradeClass;
		this.goldBase = goldBase;
		this.goldIncrement = goldIncrement;
		this.levelCount = levelCount;
		this.lumberBase = lumberBase;
		this.lumberIncrement = lumberIncrement;
		this.unitRace = unitRace;
		this.timeBase = timeBase;
		this.timeIncrement = timeIncrement;
		this.transferWithUnitOwnership = transferWithUnitOwnership;
		this.levelData = levelData;
	}

	public War3ID getTypeId() {
		return typeId;
	}

	public List<CUpgradeEffect> getUpgradeEffects() {
		return upgradeEffects;
	}

	public boolean isAppliesToAllUnits() {
		return appliesToAllUnits;
	}

	public CUpgradeClass getUpgradeClass() {
		return upgradeClass;
	}

	public int getGoldBase() {
		return goldBase;
	}

	public int getGoldIncrement() {
		return goldIncrement;
	}

	public int getLevelCount() {
		return levelCount;
	}

	public int getLumberBase() {
		return lumberBase;
	}

	public int getLumberIncrement() {
		return lumberIncrement;
	}

	public CUnitRace getUnitRace() {
		return unitRace;
	}

	public int getTimeBase() {
		return timeBase;
	}

	public int getTimeIncrement() {
		return timeIncrement;
	}

	public boolean isTransferWithUnitOwnership() {
		return transferWithUnitOwnership;
	}

	public UpgradeLevel getLevel(final int index) {
		if ((index >= 0) && (index < this.levelData.size())) {
			return this.levelData.get(index);
		}
		else {
			return null;
		}
	}

	public float getBuildTime(int techtreeUnlocked) {
		return timeBase + (timeIncrement * techtreeUnlocked);
	}

	public int getGoldCost(int unlockedCount) {
		return goldBase + (goldIncrement * unlockedCount);
	}

	public int getLumberCost(int unlockedCount) {
		return lumberBase + (lumberIncrement * unlockedCount);
	}

	public static final class UpgradeLevel {
		private final String name;
		private final List<CUnitTypeRequirement> requirements;

		public UpgradeLevel(String name, List<CUnitTypeRequirement> requirements) {
			this.name = name;
			this.requirements = requirements;
		}

		public String getName() {
			return name;
		}

		public List<CUnitTypeRequirement> getRequirements() {
			return requirements;
		}
	}

	public void apply(CSimulation simulation, CUnit unit, int i) {
		for (CUpgradeEffect upgradeEffect : getUpgradeEffects()) {
			upgradeEffect.apply(simulation, unit, i);
		}
	}

	public void apply(CSimulation simulation, int playerIndex, int i) {
		for (CUpgradeEffect upgradeEffect : getUpgradeEffects()) {
			upgradeEffect.apply(simulation, playerIndex, i);
		}
	}

	public void unapply(CSimulation simulation, CUnit unit, int i) {
		for (CUpgradeEffect upgradeEffect : getUpgradeEffects()) {
			upgradeEffect.unapply(simulation, unit, i);
		}
	}

	public void unapply(CSimulation simulation, int playerIndex, int i) {
		for (CUpgradeEffect upgradeEffect : getUpgradeEffects()) {
			upgradeEffect.unapply(simulation, playerIndex, i);
		}
	}
}
