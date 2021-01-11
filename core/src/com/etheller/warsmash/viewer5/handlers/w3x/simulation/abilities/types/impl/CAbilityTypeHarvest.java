package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
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
				levelData.getLumberCapacity());
	}

}
