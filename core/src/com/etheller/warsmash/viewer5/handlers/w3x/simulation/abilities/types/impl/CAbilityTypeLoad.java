package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold.CAbilityLoad;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CAbilityTypeLoad extends CAbilityType<CAbilityTypeLoadLevelData> {

	public CAbilityTypeLoad(final War3ID alias, final War3ID code, final List<CAbilityTypeLoadLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeLoadLevelData levelData = getLevelData(0);
		return new CAbilityLoad(handleId, getAlias(), levelData.getCastRange());
	}

	@Override
	public void setLevel(final CSimulation game, final CLevelingAbility existingAbility, final int level) {
		final CAbilityTypeLoadLevelData levelData = getLevelData(level - 1);
		final CAbilityLoad heroAbility = ((CAbilityLoad) existingAbility);

		heroAbility.setCastRange(levelData.getCastRange());

		heroAbility.setLevel(level);
	}

}
