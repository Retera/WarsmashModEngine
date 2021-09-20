package com.etheller.warsmash.viewer5.handlers.w3x.simulation.state;

import com.etheller.interpreter.ast.util.CHandle;

public enum CUnitState implements CHandle {
	LIFE,
	MAX_LIFE,
	MANA,
	MAX_MANA;

	public static CUnitState[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
