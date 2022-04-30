package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold.CAbilityDrop;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CAbilityTypeDrop extends CAbilityType<CAbilityTypeDropLevelData> {

	public CAbilityTypeDrop(final War3ID alias, final War3ID code, final List<CAbilityTypeDropLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeDropLevelData levelData = getLevelData(0);
		return new CAbilityDrop(handleId, getAlias(), levelData.getCastRange());
	}

	@Override
	public void setLevel(final CSimulation game, final CLevelingAbility existingAbility, final int level) {
		final CAbilityTypeDropLevelData levelData = getLevelData(level - 1);
		final CAbilityDrop heroAbility = ((CAbilityDrop) existingAbility);

		heroAbility.setCastRange(levelData.getCastRange());

		heroAbility.setLevel(level);
	}

}
