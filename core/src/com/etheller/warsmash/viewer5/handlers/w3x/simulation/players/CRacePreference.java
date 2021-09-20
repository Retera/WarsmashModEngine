package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import com.etheller.interpreter.ast.util.CHandle;

public enum CRacePreference implements CHandle {
	HUMAN,
	ORC,
	NIGHTELF,
	UNDEAD,
	DEMON,
	RANDOM,
	USER_SELECTABLE;

	public static CRacePreference[] VALUES = values();

	public static CRacePreference getById(final int id) {
		for (final CRacePreference type : VALUES) {
			if ((type.getId()) == id) {
				return type;
			}
		}
		return null;
	}

	public int getId() {
		return 1 << ordinal();
	}

	@Override
	public int getHandleId() {
		return getId();
	}
}
