package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeRoot;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeRootLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDefenseType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeDefinitionRoot extends AbstractCAbilityTypeDefinition<CAbilityTypeRootLevelData> {
	private static final War3ID ROOTED_ATTACK_BITS = War3ID.fromString("Roo1");
	private static final War3ID UPROOTED_ATTACK_BITS = War3ID.fromString("Roo2");
	private static final War3ID ROOTED_TURNING = War3ID.fromString("Roo3");
	private static final War3ID ROOTED_DEFENSE_TYPE = War3ID.fromString("Roo4");

	@Override
	protected CAbilityTypeRootLevelData createLevelData(final MutableGameObject abilityEditorData, final int level) {
		final String targetsAllowedAtLevelString = abilityEditorData.getFieldAsString(TARGETS_ALLOWED, level);
		int rootedAttackBits = abilityEditorData.getFieldAsInteger(ROOTED_ATTACK_BITS, level);
		int uprootedAttackBits = abilityEditorData.getFieldAsInteger(UPROOTED_ATTACK_BITS, level);
		boolean rootedTurning = abilityEditorData.getFieldAsBoolean(ROOTED_TURNING, level);
		CDefenseType rootedDefenseType = CDefenseType
				.VALUES[(abilityEditorData.getFieldAsInteger(ROOTED_DEFENSE_TYPE, level))];
		final float duration = abilityEditorData.getFieldAsFloat(DURATION, level);
		final float offDuration = abilityEditorData.getFieldAsFloat(HERO_DURATION, level);
		final EnumSet<CTargetType> targetsAllowedAtLevel = CTargetType.parseTargetTypeSet(targetsAllowedAtLevelString);
		return new CAbilityTypeRootLevelData(targetsAllowedAtLevel, rootedAttackBits, uprootedAttackBits, rootedTurning,
				rootedDefenseType, duration, offDuration);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(War3ID alias, MutableGameObject abilityEditorData,
			List<CAbilityTypeRootLevelData> levelData) {
		return new CAbilityTypeRoot(alias, abilityEditorData.getCode(), levelData);
	}
}
