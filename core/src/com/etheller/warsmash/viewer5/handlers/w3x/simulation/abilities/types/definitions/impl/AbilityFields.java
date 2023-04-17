package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import com.etheller.warsmash.util.War3ID;

public interface AbilityFields {
	public static final War3ID TARGETS_ALLOWED = War3ID.fromString("atar");
	public static final War3ID LEVELS = War3ID.fromString("alev");
	public static final War3ID CAST_RANGE = War3ID.fromString("aran");
	public static final War3ID DURATION = War3ID.fromString("adur");
	public static final War3ID HERO_DURATION = War3ID.fromString("ahdu");
	public static final War3ID AREA = War3ID.fromString("aare");
	public static final War3ID MANA_COST = War3ID.fromString("amcs");
	public static final War3ID COOLDOWN = War3ID.fromString("acdn");
	public static final War3ID CASTING_TIME = War3ID.fromString("acas");
	public static final War3ID AREA_OF_EFFECT = AREA;
	public static final War3ID BUFF = War3ID.fromString("abuf");
	public static final War3ID EFFECT = War3ID.fromString("aeff");

	public static final War3ID ANIM_NAMES = War3ID.fromString("aani");

	public static final War3ID PROJECTILE_SPEED = War3ID.fromString("amsp");
	public static final War3ID PROJECTILE_HOMING_ENABLED = War3ID.fromString("amho");

	public static final War3ID WATER_ELEMENTAL_UNIT_TYPE = War3ID.fromString("Hwe1");
	public static final War3ID WATER_ELEMENTAL_UNIT_COUNT = War3ID.fromString("Hwe2");

	public static final War3ID BLIZZARD_WAVE_COUNT = War3ID.fromString("Hbz1");
	public static final War3ID BLIZZARD_DAMAGE = War3ID.fromString("Hbz2");
	public static final War3ID BLIZZARD_SHARD_COUNT = War3ID.fromString("Hbz3");
	public static final War3ID BLIZZARD_BUILDING_REDUCTION = War3ID.fromString("Hbz4");
	public static final War3ID BLIZZARD_DAMAGE_PER_SECOND = War3ID.fromString("Hbz5");
	public static final War3ID BLIZZARD_MAX_DAMAGE_PER_WAVE = War3ID.fromString("Hbz6");

	public static final War3ID ITEM_FIGURINE_SUMMON_UNIT_TYPE_1 = War3ID.fromString("Ist1");
	public static final War3ID ITEM_FIGURINE_SUMMON_UNIT_COUNT_1 = War3ID.fromString("Isn1");
	public static final War3ID ITEM_FIGURINE_SUMMON_UNIT_TYPE_2 = War3ID.fromString("Ist2");
	public static final War3ID ITEM_FIGURINE_SUMMON_UNIT_COUNT_2 = War3ID.fromString("Isn2");

	public static final War3ID FERAL_SPIRIT_SUMMON_UNIT_TYPE_1 = War3ID.fromString("Osf1");
	public static final War3ID FERAL_SPIRIT_SUMMON_UNIT_COUNT_1 = War3ID.fromString("Osf2");

	public static final War3ID REINCARNATION_DELAY_1 = War3ID.fromString("Ore1");

	public static final War3ID ITEM_EXPERIENCE_GAINED = War3ID.fromString("Ixpg");
	public static final War3ID ITEM_LEVEL_GAINED = War3ID.fromString("Ilev");

	public static final War3ID ITEM_LIFE_GAINED = War3ID.fromString("Ilif");

	public static final War3ID ITEM_MANA = War3ID.fromString("Iman");

	public static final War3ID NEUTRAL_BUILDING_ACTIVATION_RADIUS = War3ID.fromString("Neu1");
	public static final War3ID NEUTRAL_BUILDING_INTERACTION_TYPE = War3ID.fromString("Neu2");
	public static final War3ID NEUTRAL_BUILDING_SHOW_SELECT_UNIT_BUTTON = War3ID.fromString("Neu3");
	public static final War3ID NEUTRAL_BUILDING_SHOW_UNIT_INDICATOR = War3ID.fromString("Neu4");

	public static final War3ID CHARM_MAX_CREEP_LEVEL = War3ID.fromString("Nch1");

	public static final War3ID ENTANGLE_MINE_RESULTING_TYPE = War3ID.fromString("ent1");

	public static final War3ID ENTANGLE_MINE_GOLD_PER_INTERVAL = War3ID.fromString("Egm1");
	public static final War3ID ENTANGLE_MINE_INTERVAL_DURATION = War3ID.fromString("Egm2");

	public static final War3ID EAT_TREE_RIP_DELAY = War3ID.fromString("Eat1");
	public static final War3ID EAT_TREE_EAT_DELAY = War3ID.fromString("Eat2");
	public static final War3ID EAT_TREE_HIT_POINTS_GAINED = War3ID.fromString("Eat3");

	public static final War3ID MOON_WELL_MANA_GAINED = War3ID.fromString("Mbt1");
	public static final War3ID MOON_WELL_HIT_POINTS_GAINED = War3ID.fromString("Mbt2");
	public static final War3ID MOON_WELL_AUTOCAST_REQUIREMENT = War3ID.fromString("Mbt3");
	public static final War3ID MOON_WELL_WATER_HEIGHT = War3ID.fromString("Mbt4");
	public static final War3ID MOON_WELL_REGENERATE_ONLY_AT_NIGHT = War3ID.fromString("Mbt5");
}
