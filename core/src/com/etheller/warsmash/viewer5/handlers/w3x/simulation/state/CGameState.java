package com.etheller.warsmash.viewer5.handlers.w3x.simulation.state;

import com.etheller.interpreter.ast.util.CHandle;

public enum CGameState implements CHandle {
	DIVINE_INTERVENTION,
	DISCONNECTED,
	TIME_OF_DAY;

	public static CGameState[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
