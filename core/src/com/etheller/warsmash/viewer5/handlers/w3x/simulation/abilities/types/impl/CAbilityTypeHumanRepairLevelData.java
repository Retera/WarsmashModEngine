package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

import java.util.EnumSet;

public class CAbilityTypeHumanRepairLevelData extends CAbilityTypeLevelData {

	private final float navalRangeBonus;
	private final float repairCostRatio;
	private final float repairTimeRatio;
	private final float castRange;

	public CAbilityTypeHumanRepairLevelData(EnumSet<CTargetType> targetsAllowed, float navalRangeBonus, float repairCostRatio, float repairTimeRatio, float castRange) {
		super(targetsAllowed);
		this.navalRangeBonus = navalRangeBonus;
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
}
