package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.EnumSet;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeImmolationLevelData extends CAbilityTypeLevelData {
	private final float bufferManaRequired;
	private final float damagePerInterval;
	private final float manaDrainedPerSecond;
	private final float areaOfEffect;
	private final int manaCost;
	private final float duration;
	private final War3ID buffId;

	public CAbilityTypeImmolationLevelData(final EnumSet<CTargetType> targetsAllowed, final float bufferManaRequired,
			final float damagePerInterval, final float manaDrainedPerSecond, final float areaOfEffect,
			final int manaCost, final float duration, final War3ID buffId) {
		super(targetsAllowed);
		this.bufferManaRequired = bufferManaRequired;
		this.damagePerInterval = damagePerInterval;
		this.manaDrainedPerSecond = manaDrainedPerSecond;
		this.areaOfEffect = areaOfEffect;
		this.manaCost = manaCost;
		this.duration = duration;
		this.buffId = buffId;
	}

	public float getBufferManaRequired() {
		return this.bufferManaRequired;
	}

	public float getDamagePerInterval() {
		return this.damagePerInterval;
	}

	public float getManaDrainedPerSecond() {
		return this.manaDrainedPerSecond;
	}

	public float getAreaOfEffect() {
		return this.areaOfEffect;
	}

	public int getManaCost() {
		return this.manaCost;
	}

	public float getDuration() {
		return this.duration;
	}

	public War3ID getBuffId() {
		return this.buffId;
	}
}
