package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.definitions.impl;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitTypeRequirement;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.CAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl.AbstractCAbilityTypeDefinition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderParser;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityBuilderLevelData;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.types.impl.CAbilityTypeAbilityTemplateBuilder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.data.CUnitData;

public class CAbilityTypeDefinitionAbilityTemplateBuilder
		extends AbstractCAbilityTypeDefinition<CAbilityTypeAbilityBuilderLevelData> implements CAbilityTypeDefinition {

	private AbilityBuilderParser parser;

	public CAbilityTypeDefinitionAbilityTemplateBuilder(AbilityBuilderParser abilityBuilderParser) {
		super();
		this.parser = abilityBuilderParser;
	}

	@Override
	public CAbilityType<?> createAbilityType(final War3ID alias, final GameObject abilityEditorData) {
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
	protected CAbilityTypeAbilityBuilderLevelData createLevelData(final GameObject abilityEditorData,
			final int level) {
		final String targetsAllowedAtLevelString = abilityEditorData.readSLKTag(TARGETS_ALLOWED+level);
		final float area = abilityEditorData.readSLKTagFloat(AREA+level);
		final float castRange = abilityEditorData.readSLKTagFloat(CAST_RANGE+level);
		final float castTime = abilityEditorData.readSLKTagFloat(CASTING_TIME+level);
		final float cooldown = abilityEditorData.readSLKTagFloat(COOLDOWN+level);
		final float durationHero = abilityEditorData.readSLKTagFloat(HERO_DURATION+level);
		final float durationNormal = abilityEditorData.readSLKTagFloat(DURATION+level);
		final String[] buffStrings = abilityEditorData.readSLKTag(BUFF+level).split(",");
		final String[] effectStrings = abilityEditorData.readSLKTag(EFFECT+level).split(",");
		final String unitIdStr = abilityEditorData.readSLKTag(UNIT_ID+level);
		War3ID unitId = War3ID.NONE;
		if (unitIdStr != null && !unitIdStr.isEmpty()) {
			unitId = War3ID.fromString(unitIdStr);
		}
		
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
		final int manaCost = abilityEditorData.readSLKTagInt(MANA_COST+level);
		final EnumSet<CTargetType> targetsAllowedAtLevel = CTargetType.parseTargetTypeSet(targetsAllowedAtLevelString);

		int checkDeps = abilityEditorData.readSLKTagInt(CHECK_DEPENDENCIES);
		List<CUnitTypeRequirement> requirements = null;
		if (checkDeps > 0) {
			final List<String> requirementsString = abilityEditorData.getFieldAsList(REQUIREMENTS);
			final List<String> requirementsLevelsString = abilityEditorData.getFieldAsList(REQUIREMENT_LEVELS);
			requirements = CUnitData.parseRequirements(requirementsString,
					requirementsLevelsString);
		}
		
		return new CAbilityTypeAbilityBuilderLevelData(targetsAllowedAtLevel, area, castRange, castTime, cooldown,
				durationHero, durationNormal, buffs, effects, manaCost, data, unitId, requirements);
	}

	@Override
	protected CAbilityType<?> innerCreateAbilityType(final War3ID alias, final GameObject abilityEditorData,
			final List<CAbilityTypeAbilityBuilderLevelData> levelData) {
		if (abilityEditorData != null) {
			return new CAbilityTypeAbilityTemplateBuilder(alias, abilityEditorData.getFieldAsWar3ID(CODE, -1), abilityEditorData, levelData, parser);
		} else {
			return new CAbilityTypeAbilityTemplateBuilder(alias, alias, null, null, parser);
		}
	}

}
