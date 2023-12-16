package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeItemDefenseBonus;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeItemDefenseBonusLevelData;

public class CAbilityTypeDefinitionItemDefenseBonus extends
		AbstractCAbilityTypeDefinition<CAbilityTypeItemDefenseBonusLevelData> implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeItemDefenseBonusLevelData createLevelData(final GameObject abilityEditorData,
			final int level) {
		final int defenseBonus = abilityEditorData.getFieldAsInteger(DATA_A + level, 0);
		return new CAbilityTypeItemDefenseBonusLevelData(getTargetsAllowed(abilityEditorData, level), defenseBonus);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeItemDefenseBonusLevelData> levelData) {
		return new CAbilityTypeItemDefenseBonus(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
