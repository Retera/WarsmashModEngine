package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeItemDefenseBonus;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeItemDefenseBonusLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeDefinitionItemDefenseBonus extends
		AbstractCAbilityTypeDefinition<CAbilityTypeItemDefenseBonusLevelData> implements CAbilityTypeDefinition {
	protected static final War3ID DEFENSE_BONUS = War3ID.fromString("Idef");

	@Override
	protected CAbilityTypeItemDefenseBonusLevelData createLevelData(final MutableGameObject abilityEditorData,
			final int level) {
		final String targetsAllowedAtLevelString = abilityEditorData.getFieldAsString(TARGETS_ALLOWED, level);
		final int defenseBonus = abilityEditorData.getFieldAsInteger(DEFENSE_BONUS, level);
		final EnumSet<CTargetType> targetsAllowedAtLevel = CTargetType.parseTargetTypeSet(targetsAllowedAtLevelString);
		return new CAbilityTypeItemDefenseBonusLevelData(targetsAllowedAtLevel, defenseBonus);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final MutableGameObject abilityEditorData,
			final List<CAbilityTypeItemDefenseBonusLevelData> levelData) {
		return new CAbilityTypeItemDefenseBonus(alias, abilityEditorData.getCode(), levelData);
	}

}
