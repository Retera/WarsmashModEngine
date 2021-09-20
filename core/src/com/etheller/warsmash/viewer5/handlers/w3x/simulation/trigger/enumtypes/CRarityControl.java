package com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes;

import com.etheller.interpreter.ast.util.CHandle;

public enum CRarityControl implements CHandle {
	FREQUENT,
	RARE;

	public static CRarityControl[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
