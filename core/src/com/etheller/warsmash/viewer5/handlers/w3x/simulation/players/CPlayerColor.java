package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import com.etheller.interpreter.ast.util.CHandle;

public enum CPlayerColor implements CHandle {
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
	BROWN,
	MAROON, // 1.32
	NAVY,
	TURQUOISE,
	VIOLET,
	WHEAT,
	PEACH,
	MINT,
	LAVENDER,
	COAL,
	SNOW,
	EMERALD,
	PEANUT,
	NEUTRAL_GRAY,
	NEUTRAL_GRAY2,
	NEUTRAL_GRAY3,
	NEUTRAL_GRAY4;

	public static CPlayerColor[] VALUES = values();

	public static CPlayerColor getColorByIndex(final int index) {
		if ((index >= 0) && (index < VALUES.length)) {
			return VALUES[index];
		}
		return null;
	}

	@Override
	public int getHandleId() {
		return ordinal();
	}
}
