package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityOverlayedMinableMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CAbilityTypeGoldMineOverlayed extends CAbilityType<CAbilityTypeGoldMineLevelData> {

	public CAbilityTypeGoldMineOverlayed(final War3ID alias, final War3ID code,
			final List<CAbilityTypeGoldMineLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeGoldMineLevelData levelData = getLevelData(0);
		return new CAbilityOverlayedMinableMine(handleId, getCode(), getAlias(), levelData.getMaxGold(),
				levelData.getMiningDuration(), levelData.getMiningCapacity());
	}

	@Override
	public void setLevel(final CSimulation game, final CUnit unit, final CLevelingAbility existingAbility, final int level) {
		final CAbilityTypeGoldMineLevelData levelData = getLevelData(level - 1);
		final CAbilityOverlayedMinableMine heroAbility = ((CAbilityOverlayedMinableMine) existingAbility);

		heroAbility.setMiningCapacity(levelData.getMiningCapacity());
		heroAbility.setMiningDuration(levelData.getMiningDuration());

		heroAbility.setLevel(game, unit, level);
	}
}
