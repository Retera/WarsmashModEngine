package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.definitions.impl;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.units.manager.MutableObjectData.MutableGameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class CAbilityTypeDefinitionAbilityBuilder
		extends AbstractCAbilityTypeDefinition<CAbilityTypeAbilityBuilderLevelData> implements CAbilityTypeDefinition {

	private AbilityBuilderConfiguration parser;

	public CAbilityTypeDefinitionAbilityBuilder(AbilityBuilderConfiguration abilityBuilderParser) {
		super();
		this.parser = abilityBuilderParser;
	}

	@Override
	public CAbilityType<?> createAbilityType(final War3ID alias, final MutableGameObject abilityEditorData) {
		if (abilityEditorData != null) {
			final int levels = abilityEditorData.getFieldAsInteger(LEVELS, 0);
			final List<CAbilityTypeAbilityBuilderLevelData> levelData = new ArrayList<>();
			for (int level = 1; level <= levels; level++) {
				levelData.add(createLevelData(abilityEditorData, level));
			}
			return innerCreateAbilityType(alias, abilityEditorData, levelData);
		} else {
			return innerCreateAbilityType(alias, null, null);
		}
	}

	@Override
	protected CAbilityTypeAbilityBuilderLevelData createLevelData(final MutableGameObject abilityEditorData,
			final int level) {
		final String targetsAllowedAtLevelString = abilityEditorData.getFieldAsString(TARGETS_ALLOWED, level);
		final float area = abilityEditorData.getFieldAsFloat(AREA, level);
		final float castRange = abilityEditorData.getFieldAsFloat(CAST_RANGE, level);
		final float castTime = abilityEditorData.getFieldAsFloat(CASTING_TIME, level);
		final float cooldown = abilityEditorData.getFieldAsFloat(COOLDOWN, level);
		final float durationHero = abilityEditorData.getFieldAsFloat(HERO_DURATION, level);
		final float durationNormal = abilityEditorData.getFieldAsFloat(DURATION, level);
		final String[] buffStrings = abilityEditorData.getFieldAsString(BUFF, level).split(",");
		final String[] effectStrings = abilityEditorData.getFieldAsString(EFFECT, level).split(",");
		final List<String> data = new ArrayList<>();
		List<War3ID> buffs = new ArrayList<>();
		List<War3ID> effects = new ArrayList<>();
		for (String buff : buffStrings) {
			if (buff != null && !buff.isEmpty()) {
				buffs.add(War3ID.fromString(buff));
			}
		}
		for (String effect : effectStrings) {
			if (effect != null && !effect.isEmpty()) {
				effects.add(War3ID.fromString(effect));
			}
		}
		String[] letters = { "A", "B", "C", "D", "E", "F", "G", "H", "I" };
		for (String letter : letters) {
			data.add(abilityEditorData.readSLKTag("Data" + letter + level));
		}
		final int manaCost = abilityEditorData.getFieldAsInteger(MANA_COST, level);
		final EnumSet<CTargetType> targetsAllowedAtLevel = CTargetType.parseTargetTypeSet(targetsAllowedAtLevelString);
		return new CAbilityTypeAbilityBuilderLevelData(targetsAllowedAtLevel, area, castRange, castTime, cooldown,
				durationHero, durationNormal, buffs, effects, manaCost, data);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final MutableGameObject abilityEditorData,
			final List<CAbilityTypeAbilityBuilderLevelData> levelData) {
		if (abilityEditorData != null) {
			return new CAbilityTypeAbilityBuilder(alias, abilityEditorData.getCode(), levelData, parser);
		} else {
			return new CAbilityTypeAbilityBuilder(alias, alias, null, parser);
		}
	}

}
