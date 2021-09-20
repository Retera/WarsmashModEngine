package com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes;

import com.etheller.interpreter.ast.util.CHandle;

public enum CMapDifficulty implements CHandle {
	EASY,
	NORMAL,
	HARD,
	INSANE;

	public static CMapDifficulty[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
