package com.etheller.warsmash.viewer5.handlers.w3x.simulation.item;

public enum CItemTypeJass {
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
}
