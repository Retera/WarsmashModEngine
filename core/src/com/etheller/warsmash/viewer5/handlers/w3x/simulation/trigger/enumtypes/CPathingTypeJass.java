package com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes;

import com.etheller.interpreter.ast.util.CHandle;

public enum CPathingTypeJass implements CHandle {
	ANY,
	WALKABILITY,
	FLYABILITY,
	BUILDABILITY,
	PEONHARVESTPATHING,
	BLIGHTPATHING,
	FLOATABILITY,
	AMPHIBIOUSPATHING;

	public static CPathingTypeJass[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
