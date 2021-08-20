package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CAbilityTypeHarvestLumber extends CAbilityType<CAbilityTypeHarvestLumberLevelData> {

	public CAbilityTypeHarvestLumber(final War3ID alias, final War3ID code,
			final List<CAbilityTypeHarvestLumberLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeHarvestLumberLevelData levelData = getLevelData(0);
		return new CAbilityHarvest(handleId, getAlias(), levelData.getDamageToTree(), 0, levelData.getLumberCapacity(),
				levelData.getCastRange(), levelData.getDuration());
	}

}
