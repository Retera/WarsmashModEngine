package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeHumanRepair;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeHumanRepairLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

import java.util.EnumSet;
import java.util.List;

public class CAbilityTypeDefinitionHumanRepair extends AbstractCAbilityTypeDefinition<CAbilityTypeHumanRepairLevelData>
		implements CAbilityTypeDefinition {
	protected static final War3ID COST_RATIO = War3ID.fromString("Rep1");
	protected static final War3ID TIME_RATIO = War3ID.fromString("Rep2");
	protected static final War3ID NAVAL_RANGE_BONUS = War3ID.fromString("Rep5");

	@Override
	protected CAbilityTypeHumanRepairLevelData createLevelData(final MutableGameObject abilityEditorData, final int level) {
		final String targetsAllowedAtLevelString = abilityEditorData.getFieldAsString(TARGETS_ALLOWED, level);
		final EnumSet<CTargetType> targetsAllowedAtLevel = CTargetType.parseTargetTypeSet(targetsAllowedAtLevelString);
		final float costRatio = abilityEditorData.getFieldAsFloat(COST_RATIO, level);
		final float timeRatio = abilityEditorData.getFieldAsFloat(TIME_RATIO, level);
		final float navalRangeBonus = abilityEditorData.getFieldAsFloat(NAVAL_RANGE_BONUS, level);
		final float castRange = abilityEditorData.getFieldAsFloat(CAST_RANGE, level);
		return new CAbilityTypeHumanRepairLevelData(targetsAllowedAtLevel, navalRangeBonus, costRatio, timeRatio, castRange);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final MutableGameObject abilityEditorData,
			final List<CAbilityTypeHumanRepairLevelData> levelData) {
		return new CAbilityTypeHumanRepair(alias, abilityEditorData.getCode(), levelData);
	}

}
