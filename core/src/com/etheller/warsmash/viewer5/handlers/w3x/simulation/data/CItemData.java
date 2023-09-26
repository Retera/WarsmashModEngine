package com.etheller.warsmash.viewer5.handlers.w3x.simulation.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItemType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.item.CItemTypeJass;

public class CItemData {
	private static final String ABILITY_LIST = "abilList"; // replaced from 'iabi'
	private static final String COOLDOWN_GROUP = "cooldownID"; // replaced from 'icid'
	private static final String IGNORE_COOLDOWN = "ignoreCD"; // replaced from 'iicd'
	private static final String NUMBER_OF_CHARGES = "uses"; // replaced from 'iuse'
	private static final String ACTIVELY_USED = "usable"; // replaced from 'iusa'
	private static final String PERISHABLE = "perishable"; // replaced from 'iper'
	private static final String USE_AUTOMATICALLY_WHEN_ACQUIRED = "powerup"; // replaced from 'ipow'

	private static final String GOLD_COST = "goldcost"; // replaced from 'igol'
	private static final String LUMBER_COST = "lumbercost"; // replaced from 'ilum'
	private static final String STOCK_MAX = "stockMax"; // replaced from 'isto'
	private static final String STOCK_REPLENISH_INTERVAL = "stockRegen"; // replaced from 'istr'
	private static final String STOCK_START_DELAY = "stockStart"; // replaced from 'isst'

	private static final String HIT_POINTS = "HP"; // replaced from 'ihtp'
	private static final String ARMOR_TYPE = "armor"; // replaced from 'iarm'

	private static final String LEVEL = "Level"; // replaced from 'ilev'
	private static final String LEVEL_UNCLASSIFIED = "oldLevel"; // replaced from 'ilvo'
	private static final String PRIORITY = "prio"; // replaced from 'ipri'

	private static final String SELLABLE = "sellable"; // replaced from 'isel'
	private static final String PAWNABLE = "pawnable"; // replaced from 'ipaw'

	private static final String DROPPED_WHEN_CARRIER_DIES = "drop"; // replaced from 'idrp'
	private static final String CAN_BE_DROPPED = "droppable"; // replaced from 'idro'

	private static final String VALID_TARGET_FOR_TRANSFORMATION = "morph"; // replaced from 'imor'
	private static final String INCLUDE_AS_RANDOM_CHOICE = "pickRandom"; // replaced from 'iprn'

	private final Map<Integer, RandomItemSet> levelToRandomChoices = new HashMap<>();
	private static final String CLASSIFICATION = "class"; // replaced from 'icla'

	private final Map<War3ID, CItemType> itemIdToItemType = new HashMap<>();
	private final ObjectData itemData;

	public CItemData(final ObjectData itemData) {
		this.itemData = itemData;
		// TODO the below is a bit hacky, but needed to build the list of random choices
		for (final String key : this.itemData.keySet()) {
			final GameObject mutableGameObject = this.itemData.get(key);
			getItemTypeInstance(War3ID.fromString(key), mutableGameObject);
		}
	}

	public CItem create(final CSimulation simulation, final War3ID typeId, final float x, final float y,
			final int handleId) {
		final GameObject itemType = this.itemData.get(typeId.asStringValue());
		final CItemType itemTypeInstance = getItemTypeInstance(typeId, itemType);

		return new CItem(handleId, x, y, itemTypeInstance.getMaxLife(), typeId, itemTypeInstance);
	}

	public CItemType getItemType(final War3ID typeId) {
		final GameObject itemType = this.itemData.get(typeId.asStringValue());
		if (itemType == null) {
			return null;
		}
		return getItemTypeInstance(typeId, itemType);
	}

	private CItemType getItemTypeInstance(final War3ID typeId, final GameObject itemType) {
		CItemType itemTypeInstance = this.itemIdToItemType.get(typeId);
		if (itemTypeInstance == null) {
			final List<String> abilityListStringItems = itemType.getFieldAsList(ABILITY_LIST);
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
				itemClass = CItemTypeJass.valueOf(classificationString.toUpperCase());
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
			System.err.println("chooseRandomItem: no item set: " + level);
			return null;
		}
		final List<War3ID> itemsOfClass = randomItemSet.classificationToItems.get(itemClass);
		if (itemsOfClass == null) {
			System.err.println(
					"chooseRandomItem: no items of class: " + itemsOfClass + " from " + itemClass + " " + level);
			return null;
		}
		return itemsOfClass.get(seededRandom.nextInt(itemsOfClass.size()));
	}

	private static final class RandomItemSet {
		private final List<War3ID> unclassifiedItems = new ArrayList<>();
		private final Map<CItemTypeJass, List<War3ID>> classificationToItems = new HashMap<>();
	}
}
