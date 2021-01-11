package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeHarvestLevelData extends CAbilityTypeLevelData {
	private final int damageToTree;
	private final int goldCapacity;
	private final int lumberCapacity;

	public CAbilityTypeHarvestLevelData(final EnumSet<CTargetType> targetsAllowed, final int damageToTree,
			final int goldCapacity, final int lumberCapacity) {
		super(targetsAllowed);
		this.damageToTree = damageToTree;
		this.goldCapacity = goldCapacity;
		this.lumberCapacity = lumberCapacity;
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

}
