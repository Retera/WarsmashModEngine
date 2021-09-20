package com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes;

import com.etheller.interpreter.ast.util.CHandle;

public enum CMapDensity implements CHandle {
	NONE,
	LIGHT,
	MEDIUM,
	HEAVY;

	public static CMapDensity[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
