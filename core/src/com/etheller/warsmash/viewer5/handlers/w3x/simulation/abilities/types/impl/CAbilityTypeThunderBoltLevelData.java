package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.EnumSet;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeThunderBoltLevelData extends CAbilityTypeLevelData {
	private int manaCost;
	private float damage;
	private float castRange;
	private float cooldown;
	private float duration;
	private float heroDuration;
	private War3ID buffId;

	public CAbilityTypeThunderBoltLevelData(EnumSet<CTargetType> targetsAllowed, int manaCost, float damage,
			float castRange, float cooldown, float duration, float heroDuration, War3ID buffId) {
		super(targetsAllowed);
		this.manaCost = manaCost;
		this.damage = damage;
		this.castRange = castRange;
		this.cooldown = cooldown;
		this.duration = duration;
		this.heroDuration = heroDuration;
		this.buffId = buffId;
	}

	public int getManaCost() {
		return manaCost;
	}

	public float getDamage() {
		return damage;
	}

	public float getCastRange() {
		return castRange;
	}

	public float getCooldown() {
		return cooldown;
	}

	public float getDuration() {
		return duration;
	}

	public float getHeroDuration() {
		return heroDuration;
	}

	public War3ID getBuffId() {
		return buffId;
	}
}
