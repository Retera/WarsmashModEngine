package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeNeutralBuildingLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeShopSharing;

public class CAbilityTypeDefinitionShopSharing
		extends AbstractCAbilityTypeDefinition<CAbilityTypeNeutralBuildingLevelData> implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeNeutralBuildingLevelData createLevelData(final GameObject abilityEditorData,
			final int level) {
		final float castRange = abilityEditorData.getFieldAsFloat(CAST_RANGE + level, 0);
		final float activationRadius = abilityEditorData.getFieldAsFloat(DATA_A + level, 0);
		final int interactionType = abilityEditorData.getFieldAsInteger(DATA_B + level, 0);
		final boolean showSelectUnitButton = abilityEditorData.getFieldAsBoolean(DATA_C + level, 0);
		final boolean showUnitIndicator = abilityEditorData.getFieldAsBoolean(DATA_D + level, 0);

		return new CAbilityTypeNeutralBuildingLevelData(getTargetsAllowed(abilityEditorData, level), activationRadius,
				interactionType, showSelectUnitButton, showUnitIndicator);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeNeutralBuildingLevelData> levelData) {
		return new CAbilityTypeShopSharing(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
