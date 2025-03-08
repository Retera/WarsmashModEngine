package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

import java.util.EnumSet;

public class CAbilityTypeHumanRepairLevelData extends CAbilityTypeLevelData {

	private final float navalRangeBonus;
	private final float powerBuildCostRatio;
	private final float powerBuildTimeRatio;
	private final float repairCostRatio;
	private final float repairTimeRatio;
	private final float castRange;

	public CAbilityTypeHumanRepairLevelData(EnumSet<CTargetType> targetsAllowed, float navalRangeBonus,
			final float powerBuildCostRatio, final float powerBuildTimeRatio, float repairCostRatio,
			float repairTimeRatio, float castRange) {
		super(targetsAllowed);
		this.navalRangeBonus = navalRangeBonus;
		this.powerBuildCostRatio = powerBuildCostRatio;
		this.powerBuildTimeRatio = powerBuildTimeRatio;
		this.repairCostRatio = repairCostRatio;
		this.repairTimeRatio = repairTimeRatio;
		this.castRange = castRange;
	}

	public float getCastRange() {
		return this.castRange;
	}

	public float getNavalRangeBonus() {
		return navalRangeBonus;
	}

	public float getRepairCostRatio() {
		return repairCostRatio;
	}

	public float getRepairTimeRatio() {
		return repairTimeRatio;
	}

	public float getPowerBuildCostRatio() {
		return powerBuildCostRatio;
	}

	public float getPowerBuildTimeRatio() {
		return powerBuildTimeRatio;
	}
}
