package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.test.CAbilityCarrionSwarmDummy;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CAbilityTypeCarrionSwarmDummy extends CAbilityType<CAbilityTypeCarrionSwarmDummyLevelData> {

	public CAbilityTypeCarrionSwarmDummy(final War3ID alias, final War3ID code,
			final List<CAbilityTypeCarrionSwarmDummyLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeCarrionSwarmDummyLevelData levelData = getLevelData(0);
		return new CAbilityCarrionSwarmDummy(handleId, getAlias(), levelData.getCastRange(),
				levelData.getTargetsAllowed());
	}

}
