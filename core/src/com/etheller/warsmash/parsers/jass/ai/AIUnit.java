package com.etheller.warsmash.parsers.jass.ai;

public class AIUnit {
	private int townId;

	public AIUnit(final int townId) {
		this.townId = townId;
	}

	public int getTownId() {
		return this.townId;
	}

	public void setTownId(final int townId) {
		this.townId = townId;
	}
}
