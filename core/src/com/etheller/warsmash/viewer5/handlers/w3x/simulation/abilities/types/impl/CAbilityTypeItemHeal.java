package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.CAbilityItemHeal;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.test.CAbilityChannelTest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

import java.util.List;

public class CAbilityTypeItemHeal extends CAbilityType<CAbilityTypeItemHealLevelData> {

	public CAbilityTypeItemHeal(final War3ID alias, final War3ID code,
                                final List<CAbilityTypeItemHealLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeItemHealLevelData levelData = getLevelData(0);
		return new CAbilityItemHeal(handleId, getAlias(), levelData.getLifeToRegain());
	}

}
