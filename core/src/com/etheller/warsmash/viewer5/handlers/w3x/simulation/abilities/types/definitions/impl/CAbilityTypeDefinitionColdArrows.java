package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeColdArrows;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeColdArrowsLevelData;

public class CAbilityTypeDefinitionColdArrows extends AbstractCAbilityTypeDefinition<CAbilityTypeColdArrowsLevelData> {

	@Override
	protected CAbilityTypeColdArrowsLevelData createLevelData(final GameObject abilityEditorData, final int level) {
		return new CAbilityTypeColdArrowsLevelData(getTargetsAllowed(abilityEditorData, level));
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeColdArrowsLevelData> levelData) {
		return new CAbilityTypeColdArrows(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
