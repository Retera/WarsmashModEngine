package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;

public abstract class AbstractCAbilityTypeDefinition<TYPE_LEVEL_DATA extends CAbilityTypeLevelData>
		implements CAbilityTypeDefinition {
	protected static final War3ID TARGETS_ALLOWED = War3ID.fromString("atar");
	private static final War3ID LEVELS = War3ID.fromString("alev");

	@Override
	public CAbilityType<?> createAbilityType(final War3ID alias, final MutableGameObject abilityEditorData) {
		final int levels = abilityEditorData.getFieldAsInteger(LEVELS, 0);
		final List<TYPE_LEVEL_DATA> levelData = new ArrayList<>();
		for (int level = 0; level < levels; level++) {
			levelData.add(createLevelData(abilityEditorData, level));
		}
		return innerCreateAbilityType(alias, abilityEditorData, levelData);
	}

	protected abstract TYPE_LEVEL_DATA createLevelData(MutableGameObject abilityEditorData, int level);

	protected abstract CAbilityType<?> innerCreateAbilityType(War3ID alias, MutableGameObject abilityEditorData,
			List<TYPE_LEVEL_DATA> levelData);
}
