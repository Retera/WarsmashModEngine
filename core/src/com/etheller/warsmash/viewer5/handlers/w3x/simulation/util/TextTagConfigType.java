package com.etheller.warsmash.viewer5.handlers.w3x.simulation.util;

public enum TextTagConfigType {
	GOLD("Gold"), LUMBER("Lumber"), GOLD_BOUNTY("Bounty"), LUMBER_BOUNTY("LumberBounty"), XP("XP"),
	MISS_TEXT("MissText"), CRITICAL_STRIKE("CriticalStrike"), SHADOW_STRIKE("ShadowStrike"), MANA_BURN("ManaBurn"),
	BASH("Bash");

	private final String key;

	TextTagConfigType(final String key) {
		this.key = key;
	}

	public String getKey() {
		return this.key;
	}

	public static final TextTagConfigType[] VALUES = values();
}
