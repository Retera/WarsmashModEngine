package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeHolyLightLevelData extends CAbilityTypeLevelData {
	private final float castRange;
	private final float cooldown;
	private final int healAmount;
	private final int manaCost;

	public CAbilityTypeHolyLightLevelData(final EnumSet<CTargetType> targetsAllowed, final float castRange,
			final float cooldown, final int healAmount, final int manaCost) {
		super(targetsAllowed);
		this.castRange = castRange;
		this.cooldown = cooldown;
		this.healAmount = healAmount;
		this.manaCost = manaCost;
	}

	public float getCastRange() {
		return this.castRange;
	}

	public float getCooldown() {
		return this.cooldown;
	}

	public int getHealAmount() {
		return this.healAmount;
	}

	public int getManaCost() {
		return this.manaCost;
	}

}
