package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeItemReincarnation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeItemReincarnationLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

import java.util.EnumSet;
import java.util.List;

public class CAbilityTypeDefinitionItemReincarnation
		extends AbstractCAbilityTypeDefinition<CAbilityTypeItemReincarnationLevelData> implements CAbilityTypeDefinition {
	protected static final War3ID DELAY = War3ID.fromString("Ircd");
	protected static final War3ID RESTORED_LIFE = War3ID.fromString("irc2");
	protected static final War3ID RESTORED_MANA = War3ID.fromString("irc3");

	@Override
	protected CAbilityTypeItemReincarnationLevelData createLevelData(final MutableGameObject abilityEditorData,
			final int level) {
		final String targetsAllowedAtLevelString = abilityEditorData.getFieldAsString(TARGETS_ALLOWED, level);
		final int delay = abilityEditorData.getFieldAsInteger(DELAY, level);
		final int restoredLife = abilityEditorData.getFieldAsInteger(RESTORED_LIFE, level);
		final int restoredMana = abilityEditorData.getFieldAsInteger(RESTORED_MANA, level);
		final EnumSet<CTargetType> targetsAllowedAtLevel = CTargetType.parseTargetTypeSet(targetsAllowedAtLevelString);
		return new CAbilityTypeItemReincarnationLevelData(targetsAllowedAtLevel, delay, restoredLife,
				restoredMana);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final MutableGameObject abilityEditorData,
			final List<CAbilityTypeItemReincarnationLevelData> levelData) {
		return new CAbilityTypeItemReincarnation(alias, abilityEditorData.getCode(), levelData);
	}

}
