package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeItemAttackBonusLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeItemTrashbringer;

public class CAbilityTypeDefinitionItemTrashbringer
		extends AbstractCAbilityTypeDefinition<CAbilityTypeItemAttackBonusLevelData> implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeItemAttackBonusLevelData createLevelData(final GameObject abilityEditorData,
			final int level) {
		final int attackBonus = abilityEditorData.getFieldAsInteger(DATA_A + level, 0);
		return new CAbilityTypeItemAttackBonusLevelData(getTargetsAllowed(abilityEditorData, level), attackBonus);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeItemAttackBonusLevelData> levelData) {
		return new CAbilityTypeItemTrashbringer(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
