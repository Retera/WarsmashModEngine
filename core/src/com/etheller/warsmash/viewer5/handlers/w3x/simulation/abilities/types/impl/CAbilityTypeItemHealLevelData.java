package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

import java.util.EnumSet;

public class CAbilityTypeItemHealLevelData extends CAbilityTypeLevelData {
	private final int lifeToRegain;

	public CAbilityTypeItemHealLevelData(final EnumSet<CTargetType> targetsAllowed, final int lifeToRegain) {
		super(targetsAllowed);
		this.lifeToRegain = lifeToRegain;
	}

	public int getLifeToRegain() {
		return lifeToRegain;
	}
}
