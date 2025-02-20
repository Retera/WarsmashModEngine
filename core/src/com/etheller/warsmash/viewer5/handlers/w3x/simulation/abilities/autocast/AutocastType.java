package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast;

import com.etheller.interpreter.ast.util.CHandle;

public enum AutocastType implements CHandle {
	NONE,
	LOWESTHP,
	HIGESTHP,
	ATTACKTARGETING,
	ATTACKINGALLY,
	ATTACKINGENEMY,
	NEARESTVALID,
	NEARESTENEMY,
	NOTARGET,

	ATTACKREPLACEMENT;

	@Override
	public int getHandleId() {
		return ordinal();
	}

	public static final AutocastType[] VALUES = values();
}
