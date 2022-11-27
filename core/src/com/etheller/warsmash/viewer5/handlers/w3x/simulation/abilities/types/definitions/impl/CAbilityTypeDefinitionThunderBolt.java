package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeThunderBolt;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl.CAbilityTypeThunderBoltLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeDefinitionThunderBolt extends AbstractCAbilityTypeDefinition<CAbilityTypeThunderBoltLevelData>
		implements CAbilityTypeDefinition {
	private static final War3ID DAMAGE = War3ID.fromString("Htb1");

	@Override
	protected CAbilityTypeThunderBoltLevelData createLevelData(final MutableGameObject abilityEditorData,
			final int level) {
		final String targetsAllowedAtLevelString = abilityEditorData.getFieldAsString(TARGETS_ALLOWED, level);
		final float castRange = abilityEditorData.getFieldAsFloat(CAST_RANGE, level);
		final float damage = abilityEditorData.getFieldAsFloat(DAMAGE, level);
		final float cooldown = abilityEditorData.getFieldAsFloat(COOLDOWN, level);
		final int manaCost = abilityEditorData.getFieldAsInteger(MANA_COST, level);
		final float duration = abilityEditorData.getFieldAsFloat(DURATION, level);
		final float heroDuration = abilityEditorData.getFieldAsFloat(HERO_DURATION, level);
		War3ID buffId = getBuffId(abilityEditorData, level);
		final EnumSet<CTargetType> targetsAllowedAtLevel = CTargetType.parseTargetTypeSet(targetsAllowedAtLevelString);
		return new CAbilityTypeThunderBoltLevelData(targetsAllowedAtLevel, manaCost, damage, castRange, cooldown,
				duration, heroDuration, buffId);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final MutableGameObject abilityEditorData,
			final List<CAbilityTypeThunderBoltLevelData> levelData) {
		final float projectileSpeed = abilityEditorData.getFieldAsInteger(PROJECTILE_SPEED, 0);
		final boolean homingEnabled = abilityEditorData.getFieldAsBoolean(PROJECTILE_HOMING_ENABLED, 0);
		return new CAbilityTypeThunderBolt(alias, abilityEditorData.getCode(), levelData, projectileSpeed,
				homingEnabled);
	}

}
