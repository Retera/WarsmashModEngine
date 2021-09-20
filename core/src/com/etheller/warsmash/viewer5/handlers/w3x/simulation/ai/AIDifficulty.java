package com.etheller.warsmash.viewer5.handlers.w3x.simulation.ai;

import com.etheller.interpreter.ast.util.CHandle;

public enum AIDifficulty implements CHandle {
	NEWBIE,
	NORMAL,
	INSANE;

	public static AIDifficulty[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
