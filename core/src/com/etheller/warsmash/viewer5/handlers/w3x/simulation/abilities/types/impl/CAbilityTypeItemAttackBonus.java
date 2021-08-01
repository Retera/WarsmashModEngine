package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.CAbilityItemAttackBonus;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CAbilityTypeItemAttackBonus extends CAbilityType<CAbilityTypeItemAttackBonusLevelData> {

	public CAbilityTypeItemAttackBonus(final War3ID alias, final War3ID code,
			final List<CAbilityTypeItemAttackBonusLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeItemAttackBonusLevelData levelData = getLevelData(0);
		return new CAbilityItemAttackBonus(handleId, getAlias(), levelData.getDamageBonus());
	}

}
