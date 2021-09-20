package com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes;

import com.etheller.interpreter.ast.util.CHandle;

public enum CSoundType implements CHandle {
	EFFECT,
	EFFECT_LOOPED;

	public static CSoundType[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
