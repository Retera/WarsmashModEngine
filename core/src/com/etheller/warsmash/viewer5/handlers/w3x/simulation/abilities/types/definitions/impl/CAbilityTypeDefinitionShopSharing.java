package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeNeutralBuildingLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeShopSharing;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeDefinitionShopSharing
		extends AbstractCAbilityTypeDefinition<CAbilityTypeNeutralBuildingLevelData> implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeNeutralBuildingLevelData createLevelData(final MutableGameObject abilityEditorData,
			final int level) {
		final String targetsAllowedAtLevelString = abilityEditorData.getFieldAsString(TARGETS_ALLOWED, level);
		final float castRange = abilityEditorData.getFieldAsFloat(CAST_RANGE, level);
		final float activationRadius = abilityEditorData.getFieldAsFloat(NEUTRAL_BUILDING_ACTIVATION_RADIUS, level);
		final int interactionType = abilityEditorData.getFieldAsInteger(NEUTRAL_BUILDING_INTERACTION_TYPE, level);
		final boolean showSelectUnitButton = abilityEditorData
				.getFieldAsBoolean(NEUTRAL_BUILDING_SHOW_SELECT_UNIT_BUTTON, level);
		final boolean showUnitIndicator = abilityEditorData.getFieldAsBoolean(NEUTRAL_BUILDING_SHOW_UNIT_INDICATOR,
				level);

		final EnumSet<CTargetType> targetsAllowedAtLevel = CTargetType.parseTargetTypeSet(targetsAllowedAtLevelString);
		return new CAbilityTypeNeutralBuildingLevelData(targetsAllowedAtLevel, activationRadius, interactionType,
				showSelectUnitButton, showUnitIndicator);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final MutableGameObject abilityEditorData,
			final List<CAbilityTypeNeutralBuildingLevelData> levelData) {
		return new CAbilityTypeShopSharing(alias, abilityEditorData.getCode(), levelData);
	}

}
