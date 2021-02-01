package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeHarvestLevelData extends CAbilityTypeLevelData {
	private final int damageToTree;
	private final int goldCapacity;
	private final int lumberCapacity;
	private final float castRange;
	private final float duration;

	public CAbilityTypeHarvestLevelData(final EnumSet<CTargetType> targetsAllowed, final int damageToTree,
			final int goldCapacity, final int lumberCapacity, final float castRange, final float duration) {
		super(targetsAllowed);
		this.damageToTree = damageToTree;
		this.goldCapacity = goldCapacity;
		this.lumberCapacity = lumberCapacity;
		this.castRange = castRange;
		this.duration = duration;
	}

	public int getDamageToTree() {
		return this.damageToTree;
	}

	public int getGoldCapacity() {
		return this.goldCapacity;
	}

	public int getLumberCapacity() {
		return this.lumberCapacity;
	}

	public float getCastRange() {
		return this.castRange;
	}

	public float getDuration() {
		return this.duration;
	}

}
