package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeInventoryLevelData extends CAbilityTypeLevelData {

	private final boolean canDropItems;
	private final boolean canGetItems;
	private final boolean canUseItems;
	private final boolean dropItemsOnDeath;
	private final int itemCapacity;

	public CAbilityTypeInventoryLevelData(final EnumSet<CTargetType> targetsAllowed, final boolean canDropItems,
			final boolean canGetItems, final boolean canUseItems, final boolean dropItemsOnDeath,
			final int itemCapacity) {
		super(targetsAllowed);
		this.canDropItems = canDropItems;
		this.canGetItems = canGetItems;
		this.canUseItems = canUseItems;
		this.dropItemsOnDeath = dropItemsOnDeath;
		this.itemCapacity = itemCapacity;
	}

	public boolean isCanDropItems() {
		return this.canDropItems;
	}

	public boolean isCanGetItems() {
		return this.canGetItems;
	}

	public boolean isCanUseItems() {
		return this.canUseItems;
	}

	public boolean isDropItemsOnDeath() {
		return this.dropItemsOnDeath;
	}

	public int getItemCapacity() {
		return this.itemCapacity;
	}

}
