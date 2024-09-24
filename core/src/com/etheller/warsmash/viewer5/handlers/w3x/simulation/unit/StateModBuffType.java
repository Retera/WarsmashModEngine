package com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit;

import com.etheller.interpreter.ast.util.CHandle;

public enum StateModBuffType implements CHandle {
	ETHEREAL,
	RESISTANT,
	SLEEPING,
	STUN,
	MAGIC_IMMUNE,
	MORPH_IMMUNE,
	SNARED,
	DISABLE_AUTO_ATTACK,
	DISABLE_AUTO_CAST,
	DISABLE_ASSIST_ALLY,
	DISABLE_ATTACK,
	DISABLE_MELEE_ATTACK,
	DISABLE_RANGED_ATTACK,
	DISABLE_SPECIAL_ATTACK,
	DISABLE_SPELLS,
	DISABLE_UNIT_COLLISION,
	DISABLE_BUILDING_COLLISION,
	INVULNERABLE,
	INVISIBLE,
	DETECTOR,
	DETECTED;

	@Override
	public int getHandleId() {
		return ordinal();
	}

	public static final StateModBuffType[] VALUES = values();
}