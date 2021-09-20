package com.etheller.warsmash.viewer5.handlers.w3x.simulation.item;

import com.etheller.interpreter.ast.util.CHandle;

public enum CItemTypeJass implements CHandle {
	PERMANENT,
	CHARGED,
	POWERUP,
	ARTIFACT,
	PURCHASABLE,
	CAMPAIGN,
	MISCELLANEOUS,
	UNKNOWN,
	ANY;

	public static CItemTypeJass[] VALUES = values();

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
