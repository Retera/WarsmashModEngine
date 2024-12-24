package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeWispHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeWispHarvestLevelData;

public class CAbilityTypeDefinitionWispHarvest extends AbstractCAbilityTypeDefinition<CAbilityTypeWispHarvestLevelData>
		implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeWispHarvestLevelData createLevelData(final GameObject abilityEditorData, final int level) {
		final int lumberPerInterval = abilityEditorData.getFieldAsInteger(DATA_A + level, 0);
		final float artAttachmentHeight = abilityEditorData.getFieldAsFloat(DATA_C + level, 0);
		final float castRange = abilityEditorData.getFieldAsFloat(CAST_RANGE + level, 0);
		final float duration = abilityEditorData.getFieldAsFloat(DURATION + level, 0);
		return new CAbilityTypeWispHarvestLevelData(getTargetsAllowed(abilityEditorData, level), lumberPerInterval,
				artAttachmentHeight, castRange, duration);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeWispHarvestLevelData> levelData) {
		return new CAbilityTypeWispHarvest(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
