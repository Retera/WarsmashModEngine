package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeGoldMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeGoldMineLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeDefinitionGoldMine extends AbstractCAbilityTypeDefinition<CAbilityTypeGoldMineLevelData>
		implements CAbilityTypeDefinition {
	protected static final War3ID MAX_GOLD = War3ID.fromString("Gld1");
	protected static final War3ID MINING_DURATION = War3ID.fromString("Gld2");
	protected static final War3ID MINING_CAPACITY = War3ID.fromString("Gld3");

	@Override
	protected CAbilityTypeGoldMineLevelData createLevelData(final MutableGameObject abilityEditorData,
			final int level) {
		final String targetsAllowedAtLevelString = abilityEditorData.getFieldAsString(TARGETS_ALLOWED, level);
		final EnumSet<CTargetType> targetsAllowedAtLevel = CTargetType.parseTargetTypeSet(targetsAllowedAtLevelString);
		final int maxGold = abilityEditorData.getFieldAsInteger(MAX_GOLD, level);
		final float miningDuration = abilityEditorData.getFieldAsFloat(MINING_DURATION, level);
		final int miningCapacity = abilityEditorData.getFieldAsInteger(MINING_CAPACITY, level);
		return new CAbilityTypeGoldMineLevelData(targetsAllowedAtLevel, maxGold, miningDuration, miningCapacity);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final MutableGameObject abilityEditorData,
			final List<CAbilityTypeGoldMineLevelData> levelData) {
		return new CAbilityTypeGoldMine(alias, abilityEditorData.getCode(), levelData);
	}

}
