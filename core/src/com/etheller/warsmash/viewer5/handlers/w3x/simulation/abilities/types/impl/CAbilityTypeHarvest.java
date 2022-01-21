package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CAbilityTypeHarvest extends CAbilityType<CAbilityTypeHarvestLevelData> {

	public CAbilityTypeHarvest(final War3ID alias, final War3ID code,
			final List<CAbilityTypeHarvestLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeHarvestLevelData levelData = getLevelData(0);
		return new CAbilityHarvest(handleId, getAlias(), levelData.getDamageToTree(), levelData.getGoldCapacity(),
				levelData.getLumberCapacity(), levelData.getCastRange(), levelData.getDuration());
	}

	@Override
	public void setLevel(final CSimulation game, final CLevelingAbility existingAbility, final int level) {
		final CAbilityTypeHarvestLevelData levelData = getLevelData(level - 1);
		final CAbilityHarvest heroAbility = ((CAbilityHarvest) existingAbility);

		heroAbility.setDamageToTree(levelData.getDamageToTree());
		heroAbility.setGoldCapacity(levelData.getGoldCapacity());
		heroAbility.setLumberCapacity(levelData.getLumberCapacity());
		heroAbility.setCastRange(levelData.getCastRange());
		heroAbility.setDuration(levelData.getDuration());

		heroAbility.setLevel(level);
	}
}
