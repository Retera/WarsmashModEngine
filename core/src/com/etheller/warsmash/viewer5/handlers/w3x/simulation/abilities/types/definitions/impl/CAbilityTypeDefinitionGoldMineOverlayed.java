package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeGoldMineLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeGoldMineOverlayed;

public class CAbilityTypeDefinitionGoldMineOverlayed
		extends AbstractCAbilityTypeDefinition<CAbilityTypeGoldMineLevelData> implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeGoldMineLevelData createLevelData(final GameObject abilityEditorData, final int level) {
		final int maxGold = abilityEditorData.getFieldAsInteger(DATA_A + level, 0);
		final float miningDuration = abilityEditorData.getFieldAsFloat(DATA_B + level, 0);
		final int miningCapacity = abilityEditorData.getFieldAsInteger(DATA_C + level, 0);
		return new CAbilityTypeGoldMineLevelData(getTargetsAllowed(abilityEditorData, level), maxGold, miningDuration,
				miningCapacity);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeGoldMineLevelData> levelData) {
		return new CAbilityTypeGoldMineOverlayed(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
