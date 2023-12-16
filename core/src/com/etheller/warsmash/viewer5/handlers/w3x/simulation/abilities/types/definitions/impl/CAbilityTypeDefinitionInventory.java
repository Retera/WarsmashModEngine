package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeInventory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeInventoryLevelData;

public class CAbilityTypeDefinitionInventory extends AbstractCAbilityTypeDefinition<CAbilityTypeInventoryLevelData>
		implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeInventoryLevelData createLevelData(final GameObject abilityEditorData, final int level) {
		final int itemCapacity = abilityEditorData.getFieldAsInteger(DATA_A + level, 0);
		final boolean dropItemsOnDeath = abilityEditorData.getFieldAsBoolean(DATA_B + level, 0);
		final boolean canUseItems = abilityEditorData.getFieldAsBoolean(DATA_C + level, 0);
		final boolean canGetItems = abilityEditorData.getFieldAsBoolean(DATA_D + level, 0);
		final boolean canDropItems = abilityEditorData.getFieldAsBoolean(DATA_E + level, 0);
		return new CAbilityTypeInventoryLevelData(getTargetsAllowed(abilityEditorData, level), canDropItems,
				canGetItems, canUseItems, dropItemsOnDeath, itemCapacity);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeInventoryLevelData> levelData) {
		return new CAbilityTypeInventory(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
