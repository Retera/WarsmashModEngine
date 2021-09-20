package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import com.etheller.interpreter.ast.util.CHandle;

public enum CMapControl implements CHandle {
	USER,
	COMPUTER,
	RESCUABLE,
	NEUTRAL,
	CREEP,
	NONE;

	public static CMapControl[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
