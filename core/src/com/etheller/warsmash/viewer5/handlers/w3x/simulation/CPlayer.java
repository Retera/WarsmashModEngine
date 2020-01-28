package com.etheller.warsmash.viewer5.handlers.w3x.simulation;

public class CPlayer {
	private int id;
	private int gold;
	private int lumber;

	public CPlayer(final int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public int getGold() {
		return this.gold;
	}

	public int getLumber() {
		return this.lumber;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setGold(final int gold) {
		this.gold = gold;
	}

	public void setLumber(final int lumber) {
		this.lumber = lumber;
	}
}
