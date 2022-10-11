package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeItemManaRegainLevelData extends CAbilityTypeLevelData {
	private final int manaToRegain;

	public CAbilityTypeItemManaRegainLevelData(final EnumSet<CTargetType> targetsAllowed, final int manaToRegain) {
		super(targetsAllowed);
		this.manaToRegain = manaToRegain;
	}

	public int getManaToRegain() {
		return this.manaToRegain;
	}
}
