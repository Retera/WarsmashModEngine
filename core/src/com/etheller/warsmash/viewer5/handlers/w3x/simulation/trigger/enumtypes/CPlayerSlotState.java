package com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes;

import com.etheller.interpreter.ast.util.CHandle;

public enum CPlayerSlotState implements CHandle {
	EMPTY,
	PLAYING,
	LEFT;

	public static CPlayerSlotState[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
