package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeHarvestLevelData;

public class CAbilityTypeDefinitionHarvest extends AbstractCAbilityTypeDefinition<CAbilityTypeHarvestLevelData>
		implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeHarvestLevelData createLevelData(final GameObject abilityEditorData, final int level) {
		final int damageToTree = abilityEditorData.getFieldAsInteger(DATA_A + level, 0);
		final int goldCapacity = abilityEditorData.getFieldAsInteger(DATA_B + level, 0);
		final int lumberCapacity = abilityEditorData.getFieldAsInteger(DATA_C + level, 0);
		final float castRange = abilityEditorData.getFieldAsFloat(CAST_RANGE + level, 0);
		final float duration = abilityEditorData.getFieldAsFloat(DURATION + level, 0);
		return new CAbilityTypeHarvestLevelData(getTargetsAllowed(abilityEditorData, level), damageToTree, goldCapacity,
				lumberCapacity, castRange, duration);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeHarvestLevelData> levelData) {
		return new CAbilityTypeHarvest(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
