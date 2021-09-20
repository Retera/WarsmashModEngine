package com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes;

import com.etheller.interpreter.ast.util.CHandle;

public enum CVersion implements CHandle {
	REIGN_OF_CHAOS,
	FROZEN_THRONE;

	public static CVersion[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
