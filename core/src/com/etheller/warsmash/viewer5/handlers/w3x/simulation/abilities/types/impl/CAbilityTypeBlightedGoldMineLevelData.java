package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeBlightedGoldMineLevelData extends CAbilityTypeLevelData {
	private final int goldPerInterval;
	private final float intervalDuration;
	private final int maxNumberOfMiners;
	private final float radiusOfMiningRing;

	public CAbilityTypeBlightedGoldMineLevelData(final EnumSet<CTargetType> targetsAllowed, final int goldPerInterval,
			final float intervalDuration, final int maxNumberOfMiners, final float radiusOfMiningRing) {
		super(targetsAllowed);
		this.goldPerInterval = goldPerInterval;
		this.intervalDuration = intervalDuration;
		this.maxNumberOfMiners = maxNumberOfMiners;
		this.radiusOfMiningRing = radiusOfMiningRing;
	}

	public int getGoldPerInterval() {
		return this.goldPerInterval;
	}

	public float getIntervalDuration() {
		return this.intervalDuration;
	}

	public int getMaxNumberOfMiners() {
		return this.maxNumberOfMiners;
	}

	public float getRadiusOfMiningRing() {
		return this.radiusOfMiningRing;
	}
}
