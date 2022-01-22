package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeBlightedGoldMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeBlightedGoldMineLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeDefinitionBlightedGoldMine extends
		AbstractCAbilityTypeDefinition<CAbilityTypeBlightedGoldMineLevelData> implements CAbilityTypeDefinition {
	protected static final War3ID GOLD_PER_INTERVAL = War3ID.fromString("Bgm1");
	protected static final War3ID INTERVAL_DURATION = War3ID.fromString("Bgm2");
	protected static final War3ID MAX_NUMBER_OF_MINERS = War3ID.fromString("Bgm3");
	protected static final War3ID RADIUS_OF_MINING_RING = War3ID.fromString("Bgm4");

	@Override
	protected CAbilityTypeBlightedGoldMineLevelData createLevelData(final MutableGameObject abilityEditorData,
			final int level) {
		final String targetsAllowedAtLevelString = abilityEditorData.getFieldAsString(TARGETS_ALLOWED, level);
		final EnumSet<CTargetType> targetsAllowedAtLevel = CTargetType.parseTargetTypeSet(targetsAllowedAtLevelString);
		final int goldPerInterval = abilityEditorData.getFieldAsInteger(GOLD_PER_INTERVAL, level);
		final float intervalDuration = abilityEditorData.getFieldAsFloat(INTERVAL_DURATION, level);
		final int maxNumberOfMiners = abilityEditorData.getFieldAsInteger(MAX_NUMBER_OF_MINERS, level);
		final float radiusOfMiningRing = abilityEditorData.getFieldAsFloat(RADIUS_OF_MINING_RING, level);
		return new CAbilityTypeBlightedGoldMineLevelData(targetsAllowedAtLevel, goldPerInterval, intervalDuration,
				maxNumberOfMiners, radiusOfMiningRing);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final MutableGameObject abilityEditorData,
			final List<CAbilityTypeBlightedGoldMineLevelData> levelData) {
		return new CAbilityTypeBlightedGoldMine(alias, abilityEditorData.getCode(), levelData);
	}

}
