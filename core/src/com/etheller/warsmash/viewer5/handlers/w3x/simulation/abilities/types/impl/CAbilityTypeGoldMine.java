package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityGoldMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CAbilityTypeGoldMine extends CAbilityType<CAbilityTypeGoldMineLevelData> {

	public CAbilityTypeGoldMine(final War3ID alias, final War3ID code,
			final List<CAbilityTypeGoldMineLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeGoldMineLevelData levelData = getLevelData(0);
		return new CAbilityGoldMine(handleId, getAlias(), levelData.getMaxGold(), levelData.getMiningDuration(),
				levelData.getMiningCapacity());
	}

}
