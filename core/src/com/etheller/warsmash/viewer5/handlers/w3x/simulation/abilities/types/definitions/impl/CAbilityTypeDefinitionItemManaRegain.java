package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeItemManaRegain;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeItemManaRegainLevelData;

public class CAbilityTypeDefinitionItemManaRegain
		extends AbstractCAbilityTypeDefinition<CAbilityTypeItemManaRegainLevelData> implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeItemManaRegainLevelData createLevelData(final GameObject abilityEditorData, final int level) {
		final int hitPointsGained = abilityEditorData.getFieldAsInteger(DATA_A + level, 0);
		final float cooldown = abilityEditorData.getFieldAsFloat(COOLDOWN + level, 0);
		return new CAbilityTypeItemManaRegainLevelData(getTargetsAllowed(abilityEditorData, level), hitPointsGained,
				cooldown);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeItemManaRegainLevelData> levelData) {
		return new CAbilityTypeItemManaRegain(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
