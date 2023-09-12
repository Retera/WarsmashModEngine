package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeChannelTest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeChannelTestLevelData;

public class CAbilityTypeDefinitionChannelTest extends AbstractCAbilityTypeDefinition<CAbilityTypeChannelTestLevelData>
		implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeChannelTestLevelData createLevelData(final GameObject abilityEditorData, final int level) {
		final float artDuration = abilityEditorData.getFieldAsFloat(DATA_D + level, 0);
		return new CAbilityTypeChannelTestLevelData(getTargetsAllowed(abilityEditorData, level), artDuration);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeChannelTestLevelData> levelData) {
		return new CAbilityTypeChannelTest(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
