package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import com.etheller.interpreter.ast.util.CHandle;

public class CRacePreference implements CHandle {
	private int id;

	public CRacePreference(int id) {
		this.id = id;
	}

	public int ordinal() {
		return id - 1;
	}

	public int getBitMaskValue() {
		return 1 << ordinal();
	}

	@Override
	public int getHandleId() {
		return getBitMaskValue();
	}
}
