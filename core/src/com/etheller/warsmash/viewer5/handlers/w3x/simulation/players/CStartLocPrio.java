package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import com.etheller.interpreter.ast.util.CHandle;

public enum CStartLocPrio implements CHandle {
	LOW,
	HIGH,
	NOT;

	public static CStartLocPrio[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
