package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityAcolyteHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CAbilityTypeAcolyteHarvest extends CAbilityType<CAbilityTypeAcolyteHarvestLevelData> {

	public CAbilityTypeAcolyteHarvest(final War3ID alias, final War3ID code,
			final List<CAbilityTypeAcolyteHarvestLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeAcolyteHarvestLevelData levelData = getLevelData(0);
		return new CAbilityAcolyteHarvest(handleId, getAlias(), levelData.getCastRange(), levelData.getDuration());
	}

	@Override
	public void setLevel(final CSimulation game, final CLevelingAbility existingAbility, final int level) {
		final CAbilityTypeAcolyteHarvestLevelData levelData = getLevelData(level - 1);
		final CAbilityAcolyteHarvest heroAbility = ((CAbilityAcolyteHarvest) existingAbility);

		heroAbility.setCastRange(levelData.getCastRange());
		heroAbility.setDuration(levelData.getDuration());

		heroAbility.setLevel(level);
	}
}
