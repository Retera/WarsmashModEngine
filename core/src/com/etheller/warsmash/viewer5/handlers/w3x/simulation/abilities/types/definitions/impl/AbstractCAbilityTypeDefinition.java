package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;

public abstract class AbstractCAbilityTypeDefinition<TYPE_LEVEL_DATA extends CAbilityTypeLevelData>
		implements CAbilityTypeDefinition, AbilityFields {

	@Override
	public CAbilityType<?> createAbilityType(final War3ID alias, final MutableGameObject abilityEditorData) {
		final int levels = abilityEditorData.getFieldAsInteger(LEVELS, 0);
		final List<TYPE_LEVEL_DATA> levelData = new ArrayList<>();
		for (int level = 1; level <= levels; level++) {
			levelData.add(createLevelData(abilityEditorData, level));
		}
		return innerCreateAbilityType(alias, abilityEditorData, levelData);
	}

	protected abstract TYPE_LEVEL_DATA createLevelData(MutableGameObject abilityEditorData, int level);

	protected abstract CAbilityType<?> innerCreateAbilityType(War3ID alias, MutableGameObject abilityEditorData,
			List<TYPE_LEVEL_DATA> levelData);

	public static final War3ID getBuffId(final MutableGameObject abilityEditorData, final int level) {
		return getBuffId(BUFF, abilityEditorData, level, 0);
	}

	public static final War3ID getBuffId(final MutableGameObject abilityEditorData, final int level, final int buffIndex) {
		return getBuffId(BUFF, abilityEditorData, level, buffIndex);
	}

	public static final War3ID getEffectId(final MutableGameObject abilityEditorData, final int level) {
		return getBuffId(EFFECT, abilityEditorData, level, 0);
	}

	public static final War3ID getLightningId(final MutableGameObject abilityEditorData, final int level) {
		return getBuffId(LIGHTNING, abilityEditorData, level, 0);
	}
	public static final War3ID getLightningId(final MutableGameObject abilityEditorData, final int level, final int index) {
		return getBuffId(LIGHTNING, abilityEditorData, level, index);
	}

	private static final War3ID getBuffId(final War3ID metaKey, final MutableGameObject abilityEditorData,
			final int level, int buffIndex) {
		final String buffIdString = abilityEditorData.getFieldAsString(metaKey, level);
		War3ID buffId = War3ID.NONE;
		try {
			buffId = War3ID.fromString(buffIdString.split(",")[buffIndex]);
		}
		catch (final Exception exc) {
		}
		return buffId;
	}
}
