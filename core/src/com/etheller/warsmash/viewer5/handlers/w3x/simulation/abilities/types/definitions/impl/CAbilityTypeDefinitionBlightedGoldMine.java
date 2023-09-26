package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeBlightedGoldMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeBlightedGoldMineLevelData;

public class CAbilityTypeDefinitionBlightedGoldMine extends
		AbstractCAbilityTypeDefinition<CAbilityTypeBlightedGoldMineLevelData> implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeBlightedGoldMineLevelData createLevelData(final GameObject abilityEditorData,
			final int level) {
		final int goldPerInterval = abilityEditorData.getFieldAsInteger(DATA_A + level, 0);
		final float intervalDuration = abilityEditorData.getFieldAsFloat(DATA_B + level, 0);
		final int maxNumberOfMiners = abilityEditorData.getFieldAsInteger(DATA_C + level, 0);
		final float radiusOfMiningRing = abilityEditorData.getFieldAsFloat(DATA_D + level, 0);
		return new CAbilityTypeBlightedGoldMineLevelData(getTargetsAllowed(abilityEditorData, level), goldPerInterval,
				intervalDuration, maxNumberOfMiners, radiusOfMiningRing);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeBlightedGoldMineLevelData> levelData) {
		return new CAbilityTypeBlightedGoldMine(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
