package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeGoldMineLevelData extends CAbilityTypeLevelData {
	private final int maxGold;
	private final float miningDuration;
	private final int miningCapacity;

	public CAbilityTypeGoldMineLevelData(final EnumSet<CTargetType> targetsAllowed, final int maxGold,
			final float miningDuration, final int miningCapacity) {
		super(targetsAllowed);
		this.maxGold = maxGold;
		this.miningDuration = miningDuration;
		this.miningCapacity = miningCapacity;
	}

	public int getMaxGold() {
		return this.maxGold;
	}

	public float getMiningDuration() {
		return this.miningDuration;
	}

	public int getMiningCapacity() {
		return this.miningCapacity;
	}
}
