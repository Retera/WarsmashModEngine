package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeCarrionSwarmDummy;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeCarrionSwarmDummyLevelData;

public class CAbilityTypeDefinitionCarrionSwarmDummy extends
		AbstractCAbilityTypeDefinition<CAbilityTypeCarrionSwarmDummyLevelData> implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeCarrionSwarmDummyLevelData createLevelData(final GameObject abilityEditorData,
			final int level) {
		final float castRange = abilityEditorData.getFieldAsFloat(CAST_RANGE + level, 0);
		return new CAbilityTypeCarrionSwarmDummyLevelData(getTargetsAllowed(abilityEditorData, level), castRange);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeCarrionSwarmDummyLevelData> levelData) {
		return new CAbilityTypeCarrionSwarmDummy(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
