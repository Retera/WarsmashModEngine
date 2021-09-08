package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeItemStatBonus;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeItemStatBonusLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeDefinitionItemStatBonus
		extends AbstractCAbilityTypeDefinition<CAbilityTypeItemStatBonusLevelData> implements CAbilityTypeDefinition {
	protected static final War3ID STRENGTH = War3ID.fromString("Istr");
	protected static final War3ID AGILITY = War3ID.fromString("Iagi");
	protected static final War3ID INTELLIGENCE = War3ID.fromString("Iint");

	@Override
	protected CAbilityTypeItemStatBonusLevelData createLevelData(final MutableGameObject abilityEditorData,
			final int level) {
		final String targetsAllowedAtLevelString = abilityEditorData.getFieldAsString(TARGETS_ALLOWED, level);
		final int strengthBonus = abilityEditorData.getFieldAsInteger(STRENGTH, level);
		final int agilityBonus = abilityEditorData.getFieldAsInteger(AGILITY, level);
		final int intelligenceBonus = abilityEditorData.getFieldAsInteger(INTELLIGENCE, level);
		final EnumSet<CTargetType> targetsAllowedAtLevel = CTargetType.parseTargetTypeSet(targetsAllowedAtLevelString);
		return new CAbilityTypeItemStatBonusLevelData(targetsAllowedAtLevel, strengthBonus, agilityBonus,
				intelligenceBonus);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final MutableGameObject abilityEditorData,
			final List<CAbilityTypeItemStatBonusLevelData> levelData) {
		return new CAbilityTypeItemStatBonus(alias, abilityEditorData.getCode(), levelData);
	}

}
