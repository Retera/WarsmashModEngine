package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import com.etheller.interpreter.ast.util.CHandle;

public enum CPlayerGameResult implements CHandle {
	VICTORY,
	DEFEAT,
	TIE,
	NEUTRAL;

	public static CPlayerGameResult[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
