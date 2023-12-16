package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;
import java.util.Set;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeLoad;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeLoadLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CUnitData;

public class CAbilityTypeDefinitionLoad extends AbstractCAbilityTypeDefinition<CAbilityTypeLoadLevelData>
		implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeLoadLevelData createLevelData(final GameObject abilityEditorData, final int level) {
		final float castRange = abilityEditorData.getFieldAsFloat(CAST_RANGE + level, 0);
		final List<String> allowedUnitType = abilityEditorData.getFieldAsList(UNIT_ID + level);
		final Set<War3ID> allowedUnitTypes = CUnitData.parseIDSet(allowedUnitType);

		return new CAbilityTypeLoadLevelData(getTargetsAllowed(abilityEditorData, level), castRange, allowedUnitTypes);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeLoadLevelData> levelData) {
		return new CAbilityTypeLoad(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
