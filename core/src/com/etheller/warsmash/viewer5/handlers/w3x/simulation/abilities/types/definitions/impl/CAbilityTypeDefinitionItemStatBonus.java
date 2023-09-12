package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeItemStatBonus;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeItemStatBonusLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeDefinitionItemStatBonus
		extends AbstractCAbilityTypeDefinition<CAbilityTypeItemStatBonusLevelData> implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeItemStatBonusLevelData createLevelData(final GameObject abilityEditorData, final int level) {
		final String targetsAllowedAtLevelString = abilityEditorData.getFieldAsString(TARGETS_ALLOWED, level);
		final int strengthBonus = abilityEditorData.getFieldAsInteger(DATA_C + level, 0);
		final int agilityBonus = abilityEditorData.getFieldAsInteger(DATA_A + level, 0);
		final int intelligenceBonus = abilityEditorData.getFieldAsInteger(DATA_B + level, 0);
		final EnumSet<CTargetType> targetsAllowedAtLevel = CTargetType.parseTargetTypeSet(targetsAllowedAtLevelString);
		return new CAbilityTypeItemStatBonusLevelData(targetsAllowedAtLevel, strengthBonus, agilityBonus,
				intelligenceBonus);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeItemStatBonusLevelData> levelData) {
		return new CAbilityTypeItemStatBonus(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
