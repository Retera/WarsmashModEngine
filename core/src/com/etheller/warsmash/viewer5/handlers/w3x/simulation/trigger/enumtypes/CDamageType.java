package com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes;

import com.etheller.interpreter.ast.util.CHandle;

public enum CDamageType implements CHandle {
	UNKNOWN,
	UNKNOWN_CODE_1,
	UNKNOWN_CODE_2,
	UNKNOWN_CODE_3,
	NORMAL,
	ENHANCED,
	UNKNOWN_CODE_6,
	UNKNOWN_CODE_7,
	FIRE,
	COLD,
	LIGHTNING,
	POISON,
	DISEASE,
	DIVINE,
	MAGIC,
	SONIC,
	ACID,
	FORCE,
	DEATH,
	MIND,
	PLANT,
	DEFENSIVE,
	DEMOLITION,
	SLOW_POISON,
	SPIRIT_LINK,
	SHADOW_STRIKE,
	UNIVERSAL;

	public static CDamageType[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
