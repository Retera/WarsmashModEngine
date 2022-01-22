package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeBlight;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeBlightLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeDefinitionBlight extends AbstractCAbilityTypeDefinition<CAbilityTypeBlightLevelData>
		implements CAbilityTypeDefinition {
	protected static final War3ID EXPANSION_AMOUNT = War3ID.fromString("Bli1");
	protected static final War3ID CREATES_BLIGHT = War3ID.fromString("Bli2");

	@Override
	protected CAbilityTypeBlightLevelData createLevelData(final MutableGameObject abilityEditorData, final int level) {
		final String targetsAllowedAtLevelString = abilityEditorData.getFieldAsString(TARGETS_ALLOWED, level);
		final EnumSet<CTargetType> targetsAllowedAtLevel = CTargetType.parseTargetTypeSet(targetsAllowedAtLevelString);
		final boolean createsBlight = abilityEditorData.getFieldAsBoolean(CREATES_BLIGHT, level);
		final float expansionAmount = abilityEditorData.getFieldAsFloat(EXPANSION_AMOUNT, level);
		final float area = abilityEditorData.getFieldAsFloat(AREA, level);
		final float duration = abilityEditorData.getFieldAsFloat(DURATION, level);
		return new CAbilityTypeBlightLevelData(targetsAllowedAtLevel, createsBlight, expansionAmount, area, duration);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final MutableGameObject abilityEditorData,
			final List<CAbilityTypeBlightLevelData> levelData) {
		return new CAbilityTypeBlight(alias, abilityEditorData.getCode(), levelData);
	}

}
