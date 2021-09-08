package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.item.CItemTypeJass;

public class CItemType {
	private final List<War3ID> abilityList;
	private final War3ID cooldownGroup;
	private final boolean ignoreCooldown;
	private final int numberOfCharges;
	private final boolean activelyUsed;
	private final boolean perishable;
	private final boolean useAutomaticallyWhenAcquired;
	private final int goldCost;
	private final int lumberCost;
	private final int stockMax;
	private final int stockReplenishInterval;
	private final int stockStartDelay;
	private final int maxLife;
	private final String armorType;
	private final int level;
	private final int levelUnclassified;
	private final int priority;
	private final boolean sellable;
	private final boolean pawnable;
	private final boolean droppedWhenCarrierDies;
	private final boolean canBeDropped;
	private final boolean validTargetForTransformation;
	private final boolean includeAsRandomChoice;
	private final CItemTypeJass itemClass;

	public CItemType(final List<War3ID> abilityList, final War3ID cooldownGroup, final boolean ignoreCooldown,
			final int numberOfCharges, final boolean activelyUsed, final boolean perishable,
			final boolean useAutomaticallyWhenAcquired, final int goldCost, final int lumberCost, final int stockMax,
			final int stockReplenishInterval, final int stockStartDelay, final int maxLife, final String armorType,
			final int level, final int levelUnclassified, final int priority, final boolean sellable,
			final boolean pawnable, final boolean droppedWhenCarrierDies, final boolean canBeDropped,
			final boolean validTargetForTransformation, final boolean includeAsRandomChoice,
			final CItemTypeJass itemClass) {
		this.abilityList = abilityList;
		this.cooldownGroup = cooldownGroup;
		this.ignoreCooldown = ignoreCooldown;
		this.numberOfCharges = numberOfCharges;
		this.activelyUsed = activelyUsed;
		this.perishable = perishable;
		this.useAutomaticallyWhenAcquired = useAutomaticallyWhenAcquired;
		this.goldCost = goldCost;
		this.lumberCost = lumberCost;
		this.stockMax = stockMax;
		this.stockReplenishInterval = stockReplenishInterval;
		this.stockStartDelay = stockStartDelay;
		this.maxLife = maxLife;
		this.armorType = armorType;
		this.level = level;
		this.levelUnclassified = levelUnclassified;
		this.priority = priority;
		this.sellable = sellable;
		this.pawnable = pawnable;
		this.droppedWhenCarrierDies = droppedWhenCarrierDies;
		this.canBeDropped = canBeDropped;
		this.validTargetForTransformation = validTargetForTransformation;
		this.includeAsRandomChoice = includeAsRandomChoice;
		this.itemClass = itemClass;
	}

	public List<War3ID> getAbilityList() {
		return this.abilityList;
	}

	public War3ID getCooldownGroup() {
		return this.cooldownGroup;
	}

	public boolean isIgnoreCooldown() {
		return this.ignoreCooldown;
	}

	public int getNumberOfCharges() {
		return this.numberOfCharges;
	}

	public boolean isActivelyUsed() {
		return this.activelyUsed;
	}

	public boolean isPerishable() {
		return this.perishable;
	}

	public boolean isUseAutomaticallyWhenAcquired() {
		return this.useAutomaticallyWhenAcquired;
	}

	public int getGoldCost() {
		return this.goldCost;
	}

	public int getLumberCost() {
		return this.lumberCost;
	}

	public int getStockMax() {
		return this.stockMax;
	}

	public int getStockReplenishInterval() {
		return this.stockReplenishInterval;
	}

	public int getStockStartDelay() {
		return this.stockStartDelay;
	}

	public int getMaxLife() {
		return this.maxLife;
	}

	public String getArmorType() {
		return this.armorType;
	}

	public int getLevel() {
		return this.level;
	}

	public int getLevelUnclassified() {
		return this.levelUnclassified;
	}

	public int getPriority() {
		return this.priority;
	}

	public boolean isSellable() {
		return this.sellable;
	}

	public boolean isPawnable() {
		return this.pawnable;
	}

	public boolean isDroppedWhenCarrierDies() {
		return this.droppedWhenCarrierDies;
	}

	public boolean isCanBeDropped() {
		return this.canBeDropped;
	}

	public boolean isValidTargetForTransformation() {
		return this.validTargetForTransformation;
	}

	public boolean isIncludeAsRandomChoice() {
		return this.includeAsRandomChoice;
	}

	public CItemTypeJass getItemClass() {
		return this.itemClass;
	}
}
