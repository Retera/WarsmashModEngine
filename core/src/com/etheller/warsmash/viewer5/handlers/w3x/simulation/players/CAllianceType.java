package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

public enum CAllianceType {
	PASSIVE,
	HELP_REQUEST,
	HELP_RESPONSE,
	SHARED_XP,
	SHARED_SPELLS,
	SHARED_VISION,
	SHARED_CONTROL,
	SHARED_ADVANCED_CONTROL,
	RESCUABLE,
	SHARED_VISION_FORCED;

	public static CAllianceType[] VALUES = values();
}
