package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeHolyLight;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeHolyLightLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeDefinitionHolyLight extends AbstractCAbilityTypeDefinition<CAbilityTypeHolyLightLevelData>
		implements CAbilityTypeDefinition {
	private static final War3ID HEAL_AMOUNT = War3ID.fromString("Hhb1");

	@Override
	protected CAbilityTypeHolyLightLevelData createLevelData(final MutableGameObject abilityEditorData,
			final int level) {
		final String targetsAllowedAtLevelString = abilityEditorData.getFieldAsString(TARGETS_ALLOWED, level);
		final float castRange = abilityEditorData.getFieldAsFloat(CAST_RANGE, level);
		final int healAmount = (int) abilityEditorData.getFieldAsFloat(HEAL_AMOUNT, level);
		final float cooldown = abilityEditorData.getFieldAsFloat(COOLDOWN, level);
		final int manaCost = abilityEditorData.getFieldAsInteger(MANA_COST, level);
		final EnumSet<CTargetType> targetsAllowedAtLevel = CTargetType.parseTargetTypeSet(targetsAllowedAtLevelString);
		return new CAbilityTypeHolyLightLevelData(targetsAllowedAtLevel, castRange, cooldown, healAmount, manaCost);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final MutableGameObject abilityEditorData,
			final List<CAbilityTypeHolyLightLevelData> levelData) {
		return new CAbilityTypeHolyLight(alias, abilityEditorData.getCode(), levelData);
	}

}
