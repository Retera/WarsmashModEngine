package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public abstract class AbstractCAbilityTypeDefinition<TYPE_LEVEL_DATA extends CAbilityTypeLevelData>
		implements CAbilityTypeDefinition, AbilityFields {

	@Override
	public CAbilityType<?> createAbilityType(final War3ID alias, final GameObject abilityEditorData) {
		final int levels = abilityEditorData.getFieldAsInteger(LEVELS, 0);
		final List<TYPE_LEVEL_DATA> levelData = new ArrayList<>();
		for (int level = 1; level <= levels; level++) {
			levelData.add(createLevelData(abilityEditorData, level));
		}
		return innerCreateAbilityType(alias, abilityEditorData, levelData);
	}

	protected abstract TYPE_LEVEL_DATA createLevelData(GameObject abilityEditorData, int level);

	protected abstract CAbilityType<?> innerCreateAbilityType(War3ID alias, GameObject abilityEditorData,
			List<TYPE_LEVEL_DATA> levelData);

	public static final War3ID getBuffId(final GameObject abilityEditorData, final int level) {
		return getBuffId(BUFF, abilityEditorData, level, 0);
	}

	public static final War3ID getBuffId(final GameObject abilityEditorData, final int level, final int buffIndex) {
		return getBuffId(BUFF, abilityEditorData, level, buffIndex);
	}

	public static final War3ID getEffectId(final GameObject abilityEditorData, final int level) {
		return getBuffId(EFFECT, abilityEditorData, level, 0);
	}

	public static final War3ID getLightningId(final GameObject abilityEditorData, final int level) {
		return getBuffId(LIGHTNING, abilityEditorData, -1, 0);
	}

	public static final War3ID getLightningId(final GameObject abilityEditorData, final int level, final int index) {
		return getBuffId(LIGHTNING, abilityEditorData, -1, index);
	}

	private static final War3ID getBuffId(final String metaKey, final GameObject abilityEditorData, final int level,
			final int buffIndex) {
		final String buffIdString = abilityEditorData
				.getFieldAsString(metaKey + (level == -1 ? "" : Integer.toString(level)), buffIndex);
		War3ID buffId = War3ID.NONE;
		try {
			buffId = War3ID.fromString(buffIdString);
		}
		catch (final Exception exc) {
		}
		return buffId;
	}

	public static EnumSet<CTargetType> getTargetsAllowed(final GameObject abilityEditorData, final int level) {
		return CTargetType.parseTargetTypeSet(abilityEditorData.getFieldAsList(TARGETS_ALLOWED + level));
	}
}
