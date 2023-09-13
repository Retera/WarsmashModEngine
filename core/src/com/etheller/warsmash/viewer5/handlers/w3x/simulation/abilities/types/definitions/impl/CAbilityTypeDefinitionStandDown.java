package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityTypeLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeStandDown;

public class CAbilityTypeDefinitionStandDown extends AbstractCAbilityTypeDefinition<CAbilityTypeLevelData>
		implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeLevelData createLevelData(final GameObject abilityEditorData, final int level) {
		return new CAbilityTypeLevelData(getTargetsAllowed(abilityEditorData, level));
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeLevelData> levelData) {
		return new CAbilityTypeStandDown(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
