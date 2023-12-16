package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypePhoenixFire;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypePhoenixFireLevelData;

public class CAbilityTypeDefinitionPhoenixFire extends AbstractCAbilityTypeDefinition<CAbilityTypePhoenixFireLevelData>
		implements CAbilityTypeDefinition {
	protected static final War3ID DAMAGE_PER_SECOND = War3ID.fromString("pxf2");

	@Override
	protected CAbilityTypePhoenixFireLevelData createLevelData(final GameObject abilityEditorData, final int level) {
		final float areaOfEffect = abilityEditorData.getFieldAsFloat(AREA_OF_EFFECT + level, 0);
		final float initialDamage = abilityEditorData.getFieldAsFloat(DATA_A + level, 0);
		final float damagePerSecond = abilityEditorData.getFieldAsFloat(DATA_B + level, 0);
		final float duration = abilityEditorData.getFieldAsFloat(DURATION + level, 0);
		final float cooldown = abilityEditorData.getFieldAsFloat(COOLDOWN + level, 0);
		return new CAbilityTypePhoenixFireLevelData(getTargetsAllowed(abilityEditorData, level), initialDamage,
				damagePerSecond, areaOfEffect, cooldown, duration);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypePhoenixFireLevelData> levelData) {
		return new CAbilityTypePhoenixFire(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
