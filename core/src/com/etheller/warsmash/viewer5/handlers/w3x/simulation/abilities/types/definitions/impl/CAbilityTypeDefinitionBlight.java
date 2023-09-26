package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeBlight;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeBlightLevelData;

public class CAbilityTypeDefinitionBlight extends AbstractCAbilityTypeDefinition<CAbilityTypeBlightLevelData>
		implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeBlightLevelData createLevelData(final GameObject abilityEditorData, final int level) {
		final boolean createsBlight = abilityEditorData.getFieldAsBoolean(DATA_A + level, 0);
		final float expansionAmount = abilityEditorData.getFieldAsFloat(DATA_B + level, 0);
		final float area = abilityEditorData.getFieldAsFloat(AREA + level, 0);
		final float duration = abilityEditorData.getFieldAsFloat(DURATION + level, 0);
		return new CAbilityTypeBlightLevelData(getTargetsAllowed(abilityEditorData, level), createsBlight,
				expansionAmount, area, duration);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeBlightLevelData> levelData) {
		return new CAbilityTypeBlight(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
