package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeItemHealLevelData extends CAbilityTypeLevelData {
	private final int lifeToRegain;
	private final float cooldown;

	public CAbilityTypeItemHealLevelData(final EnumSet<CTargetType> targetsAllowed, final int lifeToRegain,
			final float cooldown) {
		super(targetsAllowed);
		this.lifeToRegain = lifeToRegain;
		this.cooldown = cooldown;
	}

	public int getLifeToRegain() {
		return lifeToRegain;
	}

	public float getCooldown() {
		return cooldown;
	}
}
