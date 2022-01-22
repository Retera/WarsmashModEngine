package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityBlightedGoldMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CAbilityTypeBlightedGoldMine extends CAbilityType<CAbilityTypeBlightedGoldMineLevelData> {

	public CAbilityTypeBlightedGoldMine(final War3ID alias, final War3ID code,
			final List<CAbilityTypeBlightedGoldMineLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeBlightedGoldMineLevelData levelData = getLevelData(0);
		return new CAbilityBlightedGoldMine(handleId, getAlias(), levelData.getGoldPerInterval(),
				levelData.getIntervalDuration(), levelData.getMaxNumberOfMiners(), levelData.getRadiusOfMiningRing());
	}

	@Override
	public void setLevel(final CSimulation game, final CLevelingAbility existingAbility, final int level) {
		final CAbilityTypeBlightedGoldMineLevelData levelData = getLevelData(level - 1);
		final CAbilityBlightedGoldMine heroAbility = ((CAbilityBlightedGoldMine) existingAbility);

		heroAbility.setGoldPerInterval(levelData.getGoldPerInterval());
		heroAbility.setIntervalDuration(levelData.getIntervalDuration());
		heroAbility.setRadiusOfMiningRing(levelData.getRadiusOfMiningRing());
		heroAbility.setMaxNumberOfMiners(levelData.getMaxNumberOfMiners());

		heroAbility.setLevel(level);
	}
}
