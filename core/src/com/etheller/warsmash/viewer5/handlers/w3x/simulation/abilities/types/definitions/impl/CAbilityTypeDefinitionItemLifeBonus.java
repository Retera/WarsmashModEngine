package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeItemLifeBonus;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeItemLifeBonusLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeDefinitionItemLifeBonus
		extends AbstractCAbilityTypeDefinition<CAbilityTypeItemLifeBonusLevelData> implements CAbilityTypeDefinition {
	protected static final War3ID LIFE_BONUS = War3ID.fromString("Ilif");

	@Override
	protected CAbilityTypeItemLifeBonusLevelData createLevelData(final MutableGameObject abilityEditorData,
			final int level) {
		final String targetsAllowedAtLevelString = abilityEditorData.getFieldAsString(TARGETS_ALLOWED, level);
		final int lifeBonus = abilityEditorData.getFieldAsInteger(LIFE_BONUS, level);
		final EnumSet<CTargetType> targetsAllowedAtLevel = CTargetType.parseTargetTypeSet(targetsAllowedAtLevelString);
		return new CAbilityTypeItemLifeBonusLevelData(targetsAllowedAtLevel, lifeBonus);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final MutableGameObject abilityEditorData,
			final List<CAbilityTypeItemLifeBonusLevelData> levelData) {
		return new CAbilityTypeItemLifeBonus(alias, abilityEditorData.getCode(), levelData);
	}

}
