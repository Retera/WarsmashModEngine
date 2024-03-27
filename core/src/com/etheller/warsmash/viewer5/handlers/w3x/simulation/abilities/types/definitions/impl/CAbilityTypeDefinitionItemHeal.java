package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeItemHeal;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeItemHealLevelData;

public class CAbilityTypeDefinitionItemHeal extends AbstractCAbilityTypeDefinition<CAbilityTypeItemHealLevelData>
		implements CAbilityTypeDefinition {

	@Override
	protected CAbilityTypeItemHealLevelData createLevelData(final GameObject abilityEditorData, final int level) {
		final int hitPointsGained = abilityEditorData.getFieldAsInteger(DATA_A + level, 0);
		final float cooldown = abilityEditorData.getFieldAsFloat(COOLDOWN + level, 0);
		return new CAbilityTypeItemHealLevelData(getTargetsAllowed(abilityEditorData, level), hitPointsGained,
				cooldown);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeItemHealLevelData> levelData) {
		return new CAbilityTypeItemHeal(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}

}
