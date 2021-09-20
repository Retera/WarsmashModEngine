package com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes;

import com.etheller.interpreter.ast.util.CHandle;

public enum CTexMapFlags implements CHandle {
	NONE,
	WRAP_U,
	WRAP_V,
	WRAP_UV;

	public static CTexMapFlags[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
