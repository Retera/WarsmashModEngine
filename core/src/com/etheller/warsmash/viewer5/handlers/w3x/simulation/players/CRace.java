package com.etheller.warsmash.viewer5.handlers.w3x.simulation.players;

import com.etheller.interpreter.ast.util.CHandle;

public class CRace implements CHandle {
	private int id;

	public CRace(final int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	@Override
	public int getHandleId() {
		return getId();
	}

	public int ordinal() {
		return this.id - 1;
	}
}
