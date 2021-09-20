package com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes;

import com.etheller.interpreter.ast.util.CHandle;

public enum CBlendMode implements CHandle {
	NONE,
	KEYALPHA,
	BLEND,
	ADDITIVE,
	MODULATE,
	MODULATE_2X;

	public static CBlendMode[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
