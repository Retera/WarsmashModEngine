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

	public static final War3ID CHARM_MAX_CREEP_LEVEL = War3ID.fromString("Nch1");
}
