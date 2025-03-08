package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityHumanRepair;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CAbilityTypeHumanRepair extends CAbilityType<CAbilityTypeHumanRepairLevelData> {

	public CAbilityTypeHumanRepair(final War3ID alias, final War3ID code,
			final List<CAbilityTypeHumanRepairLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeHumanRepairLevelData levelData = getLevelData(0);
		return new CAbilityHumanRepair(handleId, getCode(), getAlias(), levelData.getTargetsAllowed(),
				levelData.getNavalRangeBonus(), levelData.getPowerBuildCostRatio(), levelData.getPowerBuildTimeRatio(),
				levelData.getRepairCostRatio(), levelData.getRepairTimeRatio(), levelData.getCastRange());
	}

	@Override
	public void setLevel(final CSimulation game, final CUnit unit, final CLevelingAbility existingAbility,
			final int level) {

		final CAbilityTypeHumanRepairLevelData levelData = getLevelData(level - 1);
		final CAbilityHumanRepair heroAbility = ((CAbilityHumanRepair) existingAbility);

		heroAbility.setTargetsAllowed(levelData.getTargetsAllowed());
		heroAbility.setNavalRangeBonus(levelData.getNavalRangeBonus());
		heroAbility.setRepairCostRatio(levelData.getRepairCostRatio());
		heroAbility.setRepairTimeRatio(levelData.getRepairTimeRatio());
		heroAbility.setCastRange(levelData.getCastRange());

		heroAbility.setLevel(game, unit, level);

	}

}
