package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeDrop;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeDropLevelData;

public class CAbilityTypeDefinitionDrop extends AbstractCAbilityTypeDefinition<CAbilityTypeDropLevelData>
		implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeDropLevelData createLevelData(final GameObject abilityEditorData, final int level) {
		final float castRange = abilityEditorData.getFieldAsFloat(CAST_RANGE + level, 0);

		return new CAbilityTypeDropLevelData(getTargetsAllowed(abilityEditorData, level), castRange);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeDropLevelData> levelData) {
		return new CAbilityTypeDrop(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
