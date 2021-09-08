package com.etheller.warsmash.viewer5.handlers.w3x.simulation.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItemType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.item.CItemTypeJass;

public class CItemData {
	private static final War3ID ABILITY_LIST = War3ID.fromString("iabi");
	private static final War3ID COOLDOWN_GROUP = War3ID.fromString("icid");
	private static final War3ID IGNORE_COOLDOWN = War3ID.fromString("iicd");
	private static final War3ID NUMBER_OF_CHARGES = War3ID.fromString("iuse");
	private static final War3ID ACTIVELY_USED = War3ID.fromString("iusa");
	private static final War3ID PERISHABLE = War3ID.fromString("iper");
	private static final War3ID USE_AUTOMATICALLY_WHEN_ACQUIRED = War3ID.fromString("ipow");

	private static final War3ID GOLD_COST = War3ID.fromString("igol");
	private static final War3ID LUMBER_COST = War3ID.fromString("ilum");
	private static final War3ID STOCK_MAX = War3ID.fromString("isto");
	private static final War3ID STOCK_REPLENISH_INTERVAL = War3ID.fromString("istr");
	private static final War3ID STOCK_START_DELAY = War3ID.fromString("isst");

	private static final War3ID HIT_POINTS = War3ID.fromString("ihtp");
	private static final War3ID ARMOR_TYPE = War3ID.fromString("iarm");

	private static final War3ID LEVEL = War3ID.fromString("ilev");
	private static final War3ID LEVEL_UNCLASSIFIED = War3ID.fromString("ilvo");
	private static final War3ID PRIORITY = War3ID.fromString("ipri");

	private static final War3ID SELLABLE = War3ID.fromString("isel");
	private static final War3ID PAWNABLE = War3ID.fromString("ipaw");

	private static final War3ID DROPPED_WHEN_CARRIER_DIES = War3ID.fromString("idrp");
	private static final War3ID CAN_BE_DROPPED = War3ID.fromString("idro");

	private static final War3ID VALID_TARGET_FOR_TRANSFORMATION = War3ID.fromString("imor");
	private static final War3ID INCLUDE_AS_RANDOM_CHOICE = War3ID.fromString("iprn");

	private final Map<Integer, RandomItemSet> levelToRandomChoices = new HashMap<>();
	private static final War3ID CLASSIFICATION = War3ID.fromString("icla");

	private final Map<War3ID, CItemType> itemIdToItemType = new HashMap<>();
	private final MutableObjectData itemData;

	public CItemData(final MutableObjectData itemData) {
		this.itemData = itemData;
		// TODO the below is a bit hacky, but needed to build the list of random choices
		for (final War3ID key : this.itemData.keySet()) {
			final MutableGameObject mutableGameObject = this.itemData.get(key);
			getItemTypeInstance(key, mutableGameObject);
		}
	}

	public CItem create(final CSimulation simulation, final War3ID typeId, final float x, final float y,
			final int handleId) {
		final MutableGameObject itemType = this.itemData.get(typeId);
		final CItemType itemTypeInstance = getItemTypeInstance(typeId, itemType);

		return new CItem(handleId, x, y, itemTypeInstance.getMaxLife(), typeId, itemTypeInstance);
	}

	public CItemType getItemType(final War3ID typeId) {
		final MutableGameObject itemType = this.itemData.get(typeId);
		if (itemType == null) {
			return null;
		}
		return getItemTypeInstance(typeId, itemType);
	}

	private CItemType getItemTypeInstance(final War3ID typeId, final MutableGameObject itemType) {
		CItemType itemTypeInstance = this.itemIdToItemType.get(typeId);
		if (itemTypeInstance == null) {
			final String abilityListString = itemType.getFieldAsString(ABILITY_LIST, 0);
			final String[] abilityListStringItems = abilityListString.split(",");
			final List<War3ID> abilityList = new ArrayList<>();
			for (final String abilityListStringItem : abilityListStringItems) {
				if (abilityListStringItem.length() == 4) {
					abilityList.add(War3ID.fromString(abilityListStringItem));
				}
			}

			final War3ID cooldownGroup;
			final String cooldownGroupString = itemType.getFieldAsString(COOLDOWN_GROUP, 0);
			if ((cooldownGroupString != null) && (cooldownGroupString.length() == 4)) {
				cooldownGroup = War3ID.fromString(cooldownGroupString);
			}
			else {
				cooldownGroup = null;
			}
			final boolean ignoreCooldown = itemType.getFieldAsBoolean(IGNORE_COOLDOWN, 0);
			final int numberOfCharges = itemType.getFieldAsInteger(NUMBER_OF_CHARGES, 0);
			final boolean activelyUsed = itemType.getFieldAsBoolean(ACTIVELY_USED, 0);
			final boolean perishable = itemType.getFieldAsBoolean(PERISHABLE, 0);
			final boolean useAutomaticallyWhenAcquired = itemType.getFieldAsBoolean(USE_AUTOMATICALLY_WHEN_ACQUIRED, 0);

			final int goldCost = itemType.getFieldAsInteger(GOLD_COST, 0);
			final int lumberCost = itemType.getFieldAsInteger(LUMBER_COST, 0);
			final int stockMax = itemType.getFieldAsInteger(STOCK_MAX, 0);
			final int stockReplenishInterval = itemType.getFieldAsInteger(STOCK_REPLENISH_INTERVAL, 0);
			final int stockStartDelay = itemType.getFieldAsInteger(STOCK_START_DELAY, 0);

			final int hitPoints = itemType.getFieldAsInteger(HIT_POINTS, 0);
			final String armorType = itemType.getFieldAsString(ARMOR_TYPE, 0);

			final int level = itemType.getFieldAsInteger(LEVEL, 0);
			final int levelUnclassified = itemType.getFieldAsInteger(LEVEL_UNCLASSIFIED, 0);
			final int priority = itemType.getFieldAsInteger(PRIORITY, 0);

			final boolean sellable = itemType.getFieldAsBoolean(SELLABLE, 0);
			final boolean pawnable = itemType.getFieldAsBoolean(PAWNABLE, 0);

			final boolean droppedWhenCarrierDies = itemType.getFieldAsBoolean(DROPPED_WHEN_CARRIER_DIES, 0);
			final boolean canBeDropped = itemType.getFieldAsBoolean(CAN_BE_DROPPED, 0);

			final boolean validTargetForTransformation = itemType.getFieldAsBoolean(VALID_TARGET_FOR_TRANSFORMATION, 0);
			final boolean includeAsRandomChoice = itemType.getFieldAsBoolean(INCLUDE_AS_RANDOM_CHOICE, 0);

			final String classificationString = itemType.getFieldAsString(CLASSIFICATION, 0);
			CItemTypeJass itemClass = CItemTypeJass.UNKNOWN;
			try {
				itemClass = CItemTypeJass.valueOf(classificationString);
			}
			catch (final Exception exc) {
				// do not bother to log this, means it didn't match constant (it's a user input)
			}

			itemTypeInstance = new CItemType(abilityList, cooldownGroup, ignoreCooldown, numberOfCharges, activelyUsed,
					perishable, useAutomaticallyWhenAcquired, goldCost, lumberCost, stockMax, stockReplenishInterval,
					stockStartDelay, hitPoints, armorType, level, levelUnclassified, priority, sellable, pawnable,
					droppedWhenCarrierDies, canBeDropped, validTargetForTransformation, includeAsRandomChoice,
					itemClass);
			this.itemIdToItemType.put(typeId, itemTypeInstance);

			if (includeAsRandomChoice) {
				RandomItemSet levelRandomChoices = this.levelToRandomChoices.get(level);
				if (levelRandomChoices == null) {
					levelRandomChoices = new RandomItemSet();
					this.levelToRandomChoices.put(level, levelRandomChoices);
				}
				List<War3ID> itemsOfClass = levelRandomChoices.classificationToItems.get(itemClass);
				if (itemsOfClass == null) {
					itemsOfClass = new ArrayList<>();
					levelRandomChoices.classificationToItems.put(itemClass, itemsOfClass);
				}
				itemsOfClass.add(typeId);
				RandomItemSet levelUnclassifiedRandomChoices = this.levelToRandomChoices.get(level);
				if (levelUnclassifiedRandomChoices == null) {
					levelUnclassifiedRandomChoices = new RandomItemSet();
					this.levelToRandomChoices.put(level, levelUnclassifiedRandomChoices);
				}
				levelUnclassifiedRandomChoices.unclassifiedItems.add(typeId);
			}
		}
		return itemTypeInstance;
	}

	public War3ID chooseRandomItem(final int level, final Random seededRandom) {
		final RandomItemSet randomItemSet = this.levelToRandomChoices.get(level);
		if (randomItemSet == null) {
			return null;
		}
		return randomItemSet.unclassifiedItems.get(seededRandom.nextInt(randomItemSet.unclassifiedItems.size()));
	}

	public War3ID chooseRandomItem(final CItemTypeJass itemClass, final int level, final Random seededRandom) {
		final RandomItemSet randomItemSet = this.levelToRandomChoices.get(level);
		if (randomItemSet == null) {
			return null;
		}
		final List<War3ID> itemsOfClass = randomItemSet.classificationToItems.get(itemClass);
		if (itemsOfClass == null) {
			return null;
		}
		return itemsOfClass.get(seededRandom.nextInt(itemsOfClass.size()));
	}

	private static final class RandomItemSet {
		private final List<War3ID> unclassifiedItems = new ArrayList<>();
		private final Map<CItemTypeJass, List<War3ID>> classificationToItems = new HashMap<>();
	}
}
