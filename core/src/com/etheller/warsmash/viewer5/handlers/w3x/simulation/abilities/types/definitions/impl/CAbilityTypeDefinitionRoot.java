package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeRoot;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeRootLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDefenseType;

public class CAbilityTypeDefinitionRoot extends AbstractCAbilityTypeDefinition<CAbilityTypeRootLevelData> {

	@Override
	protected CAbilityTypeRootLevelData createLevelData(final GameObject abilityEditorData, final int level) {
		final int rootedAttackBits = abilityEditorData.getFieldAsInteger(DATA_A + level, 0);
		final int uprootedAttackBits = abilityEditorData.getFieldAsInteger(DATA_B + level, 0);
		final boolean rootedTurning = abilityEditorData.getFieldAsBoolean(DATA_C + level, 0);
		final CDefenseType rootedDefenseType = CDefenseType.VALUES[(abilityEditorData.getFieldAsInteger(DATA_D + level,
				0))];
		final float duration = abilityEditorData.getFieldAsFloat(DURATION + level, 0);
		final float offDuration = abilityEditorData.getFieldAsFloat(HERO_DURATION + level, 0);
		return new CAbilityTypeRootLevelData(getTargetsAllowed(abilityEditorData, level), rootedAttackBits,
				uprootedAttackBits, rootedTurning, rootedDefenseType, duration, offDuration);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeRootLevelData> levelData) {
		return new CAbilityTypeRoot(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), levelData);
	}
}
