package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypePhoenixFire;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypePhoenixFireLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeDefinitionPhoenixFire extends AbstractCAbilityTypeDefinition<CAbilityTypePhoenixFireLevelData>
		implements CAbilityTypeDefinition {
	protected static final War3ID INITIAL_DAMAGE = War3ID.fromString("pxf1");
	protected static final War3ID DAMAGE_PER_SECOND = War3ID.fromString("pxf2");

	@Override
	protected CAbilityTypePhoenixFireLevelData createLevelData(final MutableGameObject abilityEditorData,
			final int level) {
		final String targetsAllowedAtLevelString = abilityEditorData.getFieldAsString(TARGETS_ALLOWED, level);
		final EnumSet<CTargetType> targetsAllowedAtLevel = CTargetType.parseTargetTypeSet(targetsAllowedAtLevelString);
		final float areaOfEffect = abilityEditorData.getFieldAsFloat(AREA_OF_EFFECT, level);
		final float initialDamage = abilityEditorData.getFieldAsFloat(INITIAL_DAMAGE, level);
		final float damagePerSecond = abilityEditorData.getFieldAsFloat(DAMAGE_PER_SECOND, level);
		final float duration = abilityEditorData.getFieldAsFloat(DURATION, level);
		final float cooldown = abilityEditorData.getFieldAsFloat(COOLDOWN, level);
		return new CAbilityTypePhoenixFireLevelData(targetsAllowedAtLevel, initialDamage, damagePerSecond, areaOfEffect,
				cooldown, duration);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final MutableGameObject abilityEditorData,
			final List<CAbilityTypePhoenixFireLevelData> levelData) {
		return new CAbilityTypePhoenixFire(alias, abilityEditorData.getCode(), levelData);
	}

}
