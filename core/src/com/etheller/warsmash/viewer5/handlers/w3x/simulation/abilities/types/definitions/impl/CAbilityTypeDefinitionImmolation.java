package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeImmolation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeImmolationLevelData;

public class CAbilityTypeDefinitionImmolation extends AbstractCAbilityTypeDefinition<CAbilityTypeImmolationLevelData>
		implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeImmolationLevelData createLevelData(final GameObject abilityEditorData, final int level) {
		final float damagePerInterval = abilityEditorData.getFieldAsFloat(DATA_A + level, 0);
		final float manaDrainedPerSecond = abilityEditorData.getFieldAsFloat(DATA_B + level, 0);
		final float bufferManaRequired = abilityEditorData.getFieldAsFloat(DATA_C + level, 0);
		final float areaOfEffect = abilityEditorData.getFieldAsFloat(AREA_OF_EFFECT + level, 0);
		final float duration = abilityEditorData.getFieldAsFloat(DURATION + level, 0);
		final int manaCost = abilityEditorData.getFieldAsInteger(MANA_COST + level, 0);
		final War3ID buffId = getBuffId(abilityEditorData, level);
		return new CAbilityTypeImmolationLevelData(getTargetsAllowed(abilityEditorData, level), bufferManaRequired,
				damagePerInterval, manaDrainedPerSecond, areaOfEffect, manaCost, duration, buffId);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeImmolationLevelData> levelData) {
		return new CAbilityTypeImmolation(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
