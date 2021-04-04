package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.combat.CAbilityColdArrows;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CAbilityTypeColdArrows extends CAbilityType<CAbilityTypeColdArrowsLevelData> {

	public CAbilityTypeColdArrows(final War3ID alias, final War3ID code,
			final List<CAbilityTypeColdArrowsLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		return new CAbilityColdArrows(getAlias(), handleId);
	}

}
