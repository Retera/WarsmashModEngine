package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

public class CUnitDeathReplacementStacking {
	private boolean allowStacking;
	private boolean allowSamePriorityStacking;

	public CUnitDeathReplacementStacking() {
		setAllowStacking(true);
		setAllowSamePriorityStacking(true);
	}

	public CUnitDeathReplacementStacking(boolean allowStacking) {
		setAllowStacking(allowStacking);
		setAllowSamePriorityStacking(true);
	}

	public CUnitDeathReplacementStacking(boolean allowStacking, boolean allowSamePriorityStacking) {
		setAllowStacking(allowStacking);
		setAllowSamePriorityStacking(allowSamePriorityStacking);
	}

	public boolean isAllowStacking() {
		return allowStacking;
	}

	public void setAllowStacking(boolean allowStacking) {
		this.allowStacking = allowStacking;
	}

	public boolean isAllowSamePriorityStacking() {
		return allowSamePriorityStacking;
	}

	public void setAllowSamePriorityStacking(boolean allowSamePriorityStacking) {
		this.allowSamePriorityStacking = allowSamePriorityStacking;
	}

}
