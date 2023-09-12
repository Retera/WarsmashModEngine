package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeReturnResources;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeReturnResourcesLevelData;

public class CAbilityTypeDefinitionReturnResources
		extends AbstractCAbilityTypeDefinition<CAbilityTypeReturnResourcesLevelData> implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeReturnResourcesLevelData createLevelData(final GameObject abilityEditorData,
			final int level) {
		final boolean acceptsGold = abilityEditorData.getFieldAsBoolean(DATA_A + level, 0);
		final boolean acceptsLumber = abilityEditorData.getFieldAsBoolean(DATA_B + level, 0);
		return new CAbilityTypeReturnResourcesLevelData(getTargetsAllowed(abilityEditorData, level), acceptsGold,
				acceptsLumber);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeReturnResourcesLevelData> levelData) {
		return new CAbilityTypeReturnResources(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
