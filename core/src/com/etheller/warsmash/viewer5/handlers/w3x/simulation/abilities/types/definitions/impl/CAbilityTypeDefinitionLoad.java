package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeLoad;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeLoadLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CUnitData;

public class CAbilityTypeDefinitionLoad extends AbstractCAbilityTypeDefinition<CAbilityTypeLoadLevelData>
		implements CAbilityTypeDefinition {
	public static final War3ID ALLOWED_UNIT_TYPE = War3ID.fromString("Loa1");

	@Override
	protected CAbilityTypeLoadLevelData createLevelData(final MutableGameObject abilityEditorData, final int level) {
		final String targetsAllowedAtLevelString = abilityEditorData.getFieldAsString(TARGETS_ALLOWED, level);
		final float castRange = abilityEditorData.getFieldAsFloat(CAST_RANGE, level);
		final String allowedUnitType = abilityEditorData.getFieldAsString(ALLOWED_UNIT_TYPE, level);
		final Set<War3ID> allowedUnitTypes = CUnitData.parseIDSet(allowedUnitType);

		final EnumSet<CTargetType> targetsAllowedAtLevel = CTargetType.parseTargetTypeSet(targetsAllowedAtLevelString);
		return new CAbilityTypeLoadLevelData(targetsAllowedAtLevel, castRange, allowedUnitTypes);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final MutableGameObject abilityEditorData,
			final List<CAbilityTypeLoadLevelData> levelData) {
		return new CAbilityTypeLoad(alias, abilityEditorData.getCode(), levelData);
	}

}
