package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeAcolyteHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeAcolyteHarvestLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeDefinitionAcolyteHarvest
		extends AbstractCAbilityTypeDefinition<CAbilityTypeAcolyteHarvestLevelData> implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeAcolyteHarvestLevelData createLevelData(final MutableGameObject abilityEditorData,
			final int level) {
		final String targetsAllowedAtLevelString = abilityEditorData.getFieldAsString(TARGETS_ALLOWED, level);
		final EnumSet<CTargetType> targetsAllowedAtLevel = CTargetType.parseTargetTypeSet(targetsAllowedAtLevelString);
		final float castRange = abilityEditorData.getFieldAsFloat(CAST_RANGE, level);
		final float duration = abilityEditorData.getFieldAsFloat(DURATION, level);
		return new CAbilityTypeAcolyteHarvestLevelData(targetsAllowedAtLevel, castRange, duration);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final MutableGameObject abilityEditorData,
			final List<CAbilityTypeAcolyteHarvestLevelData> levelData) {
		return new CAbilityTypeAcolyteHarvest(alias, abilityEditorData.getCode(), levelData);
	}

}
