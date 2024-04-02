package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement;

public enum CUnitAttackReplacementPriority {
	AUTOCAST(0),
	HIGHPRI(1),
	INVENTORY(2),
	LOWPRIORITY(3),
	NOPRIORITY(4);

	private int priority;
	
	CUnitAttackReplacementPriority(int priority) {
		this.priority = priority;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	
}
