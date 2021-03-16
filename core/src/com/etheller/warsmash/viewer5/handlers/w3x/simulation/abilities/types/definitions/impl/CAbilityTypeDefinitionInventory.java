package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeInventory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeInventoryLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeDefinitionInventory extends AbstractCAbilityTypeDefinition<CAbilityTypeInventoryLevelData>
		implements CAbilityTypeDefinition {
	protected static final War3ID ITEM_CAPACITY = War3ID.fromString("inv1");
	protected static final War3ID DROP_ITEMS_ON_DEATH = War3ID.fromString("inv2");
	protected static final War3ID CAN_USE_ITEMS = War3ID.fromString("inv3");
	protected static final War3ID CAN_GET_ITEMS = War3ID.fromString("inv4");
	protected static final War3ID CAN_DROP_ITEMS = War3ID.fromString("inv5");

	@Override
	protected CAbilityTypeInventoryLevelData createLevelData(final MutableGameObject abilityEditorData,
			final int level) {
		final String targetsAllowedAtLevelString = abilityEditorData.getFieldAsString(TARGETS_ALLOWED, level);
		final EnumSet<CTargetType> targetsAllowedAtLevel = CTargetType.parseTargetTypeSet(targetsAllowedAtLevelString);
		final int itemCapacity = abilityEditorData.getFieldAsInteger(ITEM_CAPACITY, level);
		final boolean dropItemsOnDeath = abilityEditorData.getFieldAsBoolean(DROP_ITEMS_ON_DEATH, level);
		final boolean canUseItems = abilityEditorData.getFieldAsBoolean(CAN_USE_ITEMS, level);
		final boolean canGetItems = abilityEditorData.getFieldAsBoolean(CAN_GET_ITEMS, level);
		final boolean canDropItems = abilityEditorData.getFieldAsBoolean(CAN_DROP_ITEMS, level);
		return new CAbilityTypeInventoryLevelData(targetsAllowedAtLevel, canDropItems, canGetItems, canUseItems,
				dropItemsOnDeath, itemCapacity);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final MutableGameObject abilityEditorData,
			final List<CAbilityTypeInventoryLevelData> levelData) {
		return new CAbilityTypeInventory(alias, abilityEditorData.getCode(), levelData);
	}

}
