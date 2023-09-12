package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeHarvestLumber;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeHarvestLumberLevelData;

public class CAbilityTypeDefinitionHarvestLumber
		extends AbstractCAbilityTypeDefinition<CAbilityTypeHarvestLumberLevelData> implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeHarvestLumberLevelData createLevelData(final GameObject abilityEditorData, final int level) {
		final int damageToTree = abilityEditorData.getFieldAsInteger(DATA_A + level, 0);
		final int lumberCapacity = abilityEditorData.getFieldAsInteger(DATA_B + level, 0);
		final float castRange = abilityEditorData.getFieldAsFloat(CAST_RANGE + level, 0);
		final float duration = abilityEditorData.getFieldAsFloat(DURATION + level, 0);
		return new CAbilityTypeHarvestLumberLevelData(getTargetsAllowed(abilityEditorData, level), damageToTree,
				lumberCapacity, castRange, duration);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeHarvestLumberLevelData> levelData) {
		return new CAbilityTypeHarvestLumber(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
