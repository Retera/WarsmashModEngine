package com.etheller.warsmash.viewer5.handlers.w3x.simulation.config;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CStartLocPrio;

public class War3MapConfigStartLoc {
	private float x;
	private float y;
	private int[] otherStartIndices;
	private CStartLocPrio[] otherStartLocPriorities;

	public void setX(final float x) {
		this.x = x;
	}

	public void setY(final float y) {
		this.y = y;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public int[] getOtherStartIndices() {
		return this.otherStartIndices;
	}

	public CStartLocPrio[] getOtherStartLocPriorities() {
		return this.otherStartLocPriorities;
	}

	public void setStartLocPrioCount(final int startLocPrioCount) {
		this.otherStartIndices = new int[startLocPrioCount];
		this.otherStartLocPriorities = new CStartLocPrio[startLocPrioCount];
	}

	public void setStartLocPrio(final int prioSlotIndex, final int otherStartLocIndex, final CStartLocPrio priority) {
		this.otherStartIndices[prioSlotIndex] = otherStartLocIndex;
		this.otherStartLocPriorities[prioSlotIndex] = priority;
	}
}
