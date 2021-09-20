package com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes;

import com.etheller.interpreter.ast.util.CHandle;

public enum CGameSpeed implements CHandle {
	SLOWEST,
	SLOW,
	NORMAL,
	FAST,
	FASTEST;

	public static CGameSpeed[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
