package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilitySpellBaseTypeLevelData extends CAbilityTypeLevelData {
	private final int manaCost;
	private final float castRange;
	private final float cooldown;
	private final float castingTime;

	public CAbilitySpellBaseTypeLevelData(EnumSet<CTargetType> targetsAllowed, int manaCost, float castRange,
			float cooldown, float castingTime) {
		super(targetsAllowed);
		this.manaCost = manaCost;
		this.castRange = castRange;
		this.cooldown = cooldown;
		this.castingTime = castingTime;
	}

	public int getManaCost() {
		return manaCost;
	}

	public float getCastRange() {
		return castRange;
	}

	public float getCooldown() {
		return cooldown;
	}

	public float getCastingTime() {
		return castingTime;
	}
}
