package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeImmolation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeImmolationLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeDefinitionImmolation extends AbstractCAbilityTypeDefinition<CAbilityTypeImmolationLevelData>
		implements CAbilityTypeDefinition {
	private static final War3ID DAMAGE_PER_INTERVAL = War3ID.fromString("Eim1");
	private static final War3ID MANA_DRAINED_PER_SECOND = War3ID.fromString("Eim2");
	private static final War3ID BUFFER_MANA_REQUIRED = War3ID.fromString("Eim3");

	@Override
	protected CAbilityTypeImmolationLevelData createLevelData(final MutableGameObject abilityEditorData,
			final int level) {
		final String targetsAllowedAtLevelString = abilityEditorData.getFieldAsString(TARGETS_ALLOWED, level);
		final float damagePerInterval = abilityEditorData.getFieldAsFloat(DAMAGE_PER_INTERVAL, level);
		final float manaDrainedPerSecond = abilityEditorData.getFieldAsFloat(MANA_DRAINED_PER_SECOND, level);
		final float bufferManaRequired = abilityEditorData.getFieldAsFloat(BUFFER_MANA_REQUIRED, level);
		final float areaOfEffect = abilityEditorData.getFieldAsFloat(AREA_OF_EFFECT, level);
		final float duration = abilityEditorData.getFieldAsFloat(DURATION, level);
		final int manaCost = abilityEditorData.getFieldAsInteger(MANA_COST, level);
		final EnumSet<CTargetType> targetsAllowedAtLevel = CTargetType.parseTargetTypeSet(targetsAllowedAtLevelString);
		return new CAbilityTypeImmolationLevelData(targetsAllowedAtLevel, bufferManaRequired, damagePerInterval,
				manaDrainedPerSecond, areaOfEffect, manaCost, duration);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final MutableGameObject abilityEditorData,
			final List<CAbilityTypeImmolationLevelData> levelData) {
		return new CAbilityTypeImmolation(alias, abilityEditorData.getCode(), levelData);
	}

}
