package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeChannelTest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeItemHeal;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeItemHealLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

import java.util.EnumSet;
import java.util.List;

public class CAbilityTypeDefinitionItemHeal extends AbstractCAbilityTypeDefinition<CAbilityTypeItemHealLevelData>
		implements CAbilityTypeDefinition {
	protected static final War3ID HIT_POINTS_GAINED = War3ID.fromString("Ihpg");

	@Override
	protected CAbilityTypeItemHealLevelData createLevelData(final MutableGameObject abilityEditorData,
			final int level) {
		final String targetsAllowedAtLevelString = abilityEditorData.getFieldAsString(TARGETS_ALLOWED, level);
		final int hitPointsGained = abilityEditorData.getFieldAsInteger(HIT_POINTS_GAINED, level);
		final EnumSet<CTargetType> targetsAllowedAtLevel = CTargetType.parseTargetTypeSet(targetsAllowedAtLevelString);
		return new CAbilityTypeItemHealLevelData(targetsAllowedAtLevel, hitPointsGained);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final MutableGameObject abilityEditorData,
			final List<CAbilityTypeItemHealLevelData> levelData) {
		return new CAbilityTypeItemHeal(alias, abilityEditorData.getCode(), levelData);
	}

}
