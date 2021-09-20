package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import com.etheller.interpreter.ast.util.CHandle;

public enum CMapPlacement implements CHandle {
	RANDOM,
	FIXED,
	USE_MAP_SETTINGS,
	TEAMS_TOGETHER;

	public static CMapPlacement[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
