package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeWispHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeWispHarvestLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

import java.util.EnumSet;
import java.util.List;

public class CAbilityTypeDefinitionWispHarvest extends AbstractCAbilityTypeDefinition<CAbilityTypeWispHarvestLevelData>
		implements CAbilityTypeDefinition {
	protected static final War3ID LUMBER_PER_INTERVAL = War3ID.fromString("Wha1");
//	protected static final War3ID MAYBE_UNUSED = War3ID.fromString("Wha2");
	protected static final War3ID ART_ATTACHMENT_HEIGHT = War3ID.fromString("Wha3");

	@Override
	protected CAbilityTypeWispHarvestLevelData createLevelData(final MutableGameObject abilityEditorData, final int level) {
		final String targetsAllowedAtLevelString = abilityEditorData.getFieldAsString(TARGETS_ALLOWED, level);
		final EnumSet<CTargetType> targetsAllowedAtLevel = CTargetType.parseTargetTypeSet(targetsAllowedAtLevelString);
		final int lumberPerInterval = abilityEditorData.getFieldAsInteger(LUMBER_PER_INTERVAL, level);
		final float artAttachmentHeight = abilityEditorData.getFieldAsFloat(ART_ATTACHMENT_HEIGHT, level);
		final float castRange = abilityEditorData.getFieldAsFloat(CAST_RANGE, level);
		final float duration = abilityEditorData.getFieldAsFloat(DURATION, level);
		return new CAbilityTypeWispHarvestLevelData(targetsAllowedAtLevel, lumberPerInterval, artAttachmentHeight,
				castRange, duration);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final MutableGameObject abilityEditorData,
			final List<CAbilityTypeWispHarvestLevelData> levelData) {
		return new CAbilityTypeWispHarvest(alias, abilityEditorData.getCode(), levelData);
	}

}
