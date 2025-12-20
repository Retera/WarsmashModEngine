package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.definitions.impl;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.template.DataFieldLetter;

public interface AbilityFields {
	public static final String CODE = "code";
	public static final String DATA = "Data";
	public static final String DATA_A = DATA + DataFieldLetter.A;
	public static final String DATA_B = DATA + DataFieldLetter.B;
	public static final String DATA_C = DATA + DataFieldLetter.C;
	public static final String DATA_D = DATA + DataFieldLetter.D;
	public static final String DATA_E = DATA + DataFieldLetter.E;
	public static final String DATA_F = DATA + DataFieldLetter.F;
	public static final String DATA_G = DATA + DataFieldLetter.G;
	public static final String DATA_H = DATA + DataFieldLetter.H;
	public static final String UNIT_ID = "UnitID";
	public static final String TARGETS_ALLOWED = "targs"; // replaced from 'atar'
	public static final String LEVELS = "levels"; // replaced from 'alev'
	public static final String CAST_RANGE = "Rng"; // replaced from 'aran'
	public static final String DURATION = "Dur"; // replaced from 'adur'
	public static final String HERO_DURATION = "HeroDur"; // replaced from 'ahdu'
	public static final String AREA = "Area"; // replaced from 'aare'
	public static final String MANA_COST = "Cost"; // replaced from 'amcs'
	public static final String COOLDOWN = "Cool"; // replaced from 'acdn'
	public static final String CASTING_TIME = "Cast"; // replaced from 'acas'
	public static final String AREA_OF_EFFECT = AREA;
	public static final String BUFF = "BuffID"; // replaced from 'abuf'
	public static final String EFFECT = "EfctID"; // replaced from 'aeff'

	public static final String ANIM_NAMES = "Animnames"; // replaced from 'aani'

	public static final String PROJECTILE_SPEED = "Missilespeed"; // replaced from 'amsp'
	public static final String PROJECTILE_HOMING_ENABLED = "MissileHoming"; // replaced from 'amho'

	public static final String LIGHTNING = "LightningEffect"; // replaced from 'alig'
	public static final String REQUIRED_LEVEL = "reqLevel"; // replaced from 'arlv'
	public static final String REQUIRED_LEVEL_SKIP = "levelSkip"; // replaced from 'alsk'

	public static final String CHECK_DEPENDENCIES = "checkDep";
	public static final String REQUIREMENTS = "Requires";
	public static final String REQUIREMENT_LEVELS = "Requiresamount";

}
