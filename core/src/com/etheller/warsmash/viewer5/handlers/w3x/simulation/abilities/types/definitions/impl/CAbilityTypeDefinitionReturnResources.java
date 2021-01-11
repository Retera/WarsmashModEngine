package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeReturnResources;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeReturnResourcesLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeDefinitionReturnResources
		extends AbstractCAbilityTypeDefinition<CAbilityTypeReturnResourcesLevelData> implements CAbilityTypeDefinition {
	protected static final War3ID ACCEPTS_GOLD = War3ID.fromString("Rtn1");
	protected static final War3ID ACCEPTS_LUMBER = War3ID.fromString("Rtn2");

	@Override
	protected CAbilityTypeReturnResourcesLevelData createLevelData(final MutableGameObject abilityEditorData,
			final int level) {
		final String targetsAllowedAtLevelString = abilityEditorData.getFieldAsString(TARGETS_ALLOWED, level);
		final EnumSet<CTargetType> targetsAllowedAtLevel = CTargetType.parseTargetTypeSet(targetsAllowedAtLevelString);
		final boolean acceptsGold = abilityEditorData.getFieldAsBoolean(ACCEPTS_GOLD, level);
		final boolean acceptsLumber = abilityEditorData.getFieldAsBoolean(ACCEPTS_LUMBER, level);
		return new CAbilityTypeReturnResourcesLevelData(targetsAllowedAtLevel, acceptsGold, acceptsLumber);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final MutableGameObject abilityEditorData,
			final List<CAbilityTypeReturnResourcesLevelData> levelData) {
		return new CAbilityTypeReturnResources(alias, abilityEditorData.getCode(), levelData);
	}

}
