package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeItemLifeBonus;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeItemLifeBonusLevelData;

public class CAbilityTypeDefinitionItemLifeBonus
		extends AbstractCAbilityTypeDefinition<CAbilityTypeItemLifeBonusLevelData> implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeItemLifeBonusLevelData createLevelData(final GameObject abilityEditorData, final int level) {
		final int lifeBonus = abilityEditorData.getFieldAsInteger(DATA_A + level, 0);
		return new CAbilityTypeItemLifeBonusLevelData(getTargetsAllowed(abilityEditorData, level), lifeBonus);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeItemLifeBonusLevelData> levelData) {
		return new CAbilityTypeItemLifeBonus(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
