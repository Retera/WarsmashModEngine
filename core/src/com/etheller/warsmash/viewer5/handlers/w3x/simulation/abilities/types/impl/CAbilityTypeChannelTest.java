package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CLevelingAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.test.CAbilityChannelTest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CAbilityTypeChannelTest extends CAbilityType<CAbilityTypeChannelTestLevelData> {

	public CAbilityTypeChannelTest(final War3ID alias, final War3ID code,
			final List<CAbilityTypeChannelTestLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeChannelTestLevelData levelData = getLevelData(0);
		return new CAbilityChannelTest(handleId, getAlias(), levelData.getArtDuration());
	}

	@Override
	public void setLevel(final CSimulation game, final CLevelingAbility existingAbility, final int level) {
		final CAbilityTypeChannelTestLevelData levelData = getLevelData(level - 1);
		final CAbilityChannelTest heroAbility = ((CAbilityChannelTest) existingAbility);
		heroAbility.setArtDuration(levelData.getArtDuration());
		heroAbility.setLevel(level);
	}

}
