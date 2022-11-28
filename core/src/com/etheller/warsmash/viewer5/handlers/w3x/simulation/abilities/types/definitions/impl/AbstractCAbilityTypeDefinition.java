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
	protected static final War3ID CAST_RANGE = War3ID.fromString("aran");
	protected static final War3ID DURATION = War3ID.fromString("adur");
	protected static final War3ID HERO_DURATION = War3ID.fromString("ahdu");
	protected static final War3ID AREA = War3ID.fromString("aare");
	protected static final War3ID MANA_COST = War3ID.fromString("amcs");
	protected static final War3ID COOLDOWN = War3ID.fromString("acdn");
	protected static final War3ID AREA_OF_EFFECT = AREA;
	protected static final War3ID BUFF = War3ID.fromString("abuf");

	protected static final War3ID PROJECTILE_SPEED = War3ID.fromString("amsp");
	protected static final War3ID PROJECTILE_HOMING_ENABLED = War3ID.fromString("amho");

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

	protected static final War3ID getBuffId(final MutableGameObject abilityEditorData, final int level) {
		final String buffIdString = abilityEditorData.getFieldAsString(BUFF, level);
		War3ID buffId = War3ID.NONE;
		try {
			buffId = War3ID.fromString(buffIdString.split(",")[0]);
		} catch (final Exception exc) {
		}
		return buffId;
	}
}
