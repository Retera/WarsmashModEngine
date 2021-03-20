package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

public enum CPlayerColor {
	RED,
	BLUE,
	CYAN,
	PURPLE,
	YELLOW,
	ORANGE,
	GREEN,
	PINK,
	LIGHT_GRAY,
	LIGHT_BLUE,
	AQUA,
	BROWN;

	public static CPlayerColor[] VALUES = values();

	public static CPlayerColor getColorByIndex(final int index) {
		if ((index >= 0) && (index < VALUES.length)) {
			return VALUES[index];
		}
		return null;
	}
}
