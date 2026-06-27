package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeBag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeBagLevelData;

/**
 * Builds the "Abag" ability type from object-editor data. Mirrors the inventory
 * ability (AInv): the number of bag slots is read from DataA1 (per level), so
 * custom abilities/items can define bags of different sizes in the world editor.
 */
public class CAbilityTypeDefinitionBag extends AbstractCAbilityTypeDefinition<CAbilityTypeBagLevelData>
		implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeBagLevelData createLevelData(final GameObject abilityEditorData, final int level) {
		final int slotCount = abilityEditorData.getFieldAsInteger(DATA_A + level, 0);
		return new CAbilityTypeBagLevelData(getTargetsAllowed(abilityEditorData, level), slotCount);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeBagLevelData> levelData) {
		return new CAbilityTypeBag(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
