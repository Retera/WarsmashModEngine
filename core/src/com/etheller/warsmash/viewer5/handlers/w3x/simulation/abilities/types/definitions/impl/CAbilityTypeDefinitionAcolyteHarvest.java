package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeAcolyteHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeAcolyteHarvestLevelData;

public class CAbilityTypeDefinitionAcolyteHarvest
		extends AbstractCAbilityTypeDefinition<CAbilityTypeAcolyteHarvestLevelData> implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeAcolyteHarvestLevelData createLevelData(final GameObject abilityEditorData, final int level) {
		final float castRange = abilityEditorData.getFieldAsFloat(CAST_RANGE + level, 0);
		final float duration = abilityEditorData.getFieldAsFloat(DURATION + level, 0);
		return new CAbilityTypeAcolyteHarvestLevelData(getTargetsAllowed(abilityEditorData, level), castRange,
				duration);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeAcolyteHarvestLevelData> levelData) {
		return new CAbilityTypeAcolyteHarvest(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
