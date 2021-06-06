package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.combat.CAbilityInvulnerable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;

public class CAbilityTypeInvulnerable extends CAbilityType<CAbilityTypeLevelData> {

	public CAbilityTypeInvulnerable(final War3ID alias, final War3ID code,
			final List<CAbilityTypeLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeLevelData levelData = getLevelData(0);
		return new CAbilityInvulnerable(handleId, getAlias());
	}

}
