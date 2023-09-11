package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeItemManaRegainLevelData extends CAbilityTypeLevelData {
	private final int manaToRegain;
	private final float cooldown;

	public CAbilityTypeItemManaRegainLevelData(final EnumSet<CTargetType> targetsAllowed, final int manaToRegain,
			final float cooldown) {
		super(targetsAllowed);
		this.manaToRegain = manaToRegain;
		this.cooldown = cooldown;
	}

	public int getManaToRegain() {
		return this.manaToRegain;
	}

	public float getCooldown() {
		return cooldown;
	}
}
