package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

public enum CUnitAttackPreDamageListenerPriority {
	WINDWALK(0),
	ACCURACY(1),
	STACKING(2),
	CRITBASH(3),
	ATTACKREPLACEMENT(4);

	private int priority;
	
	CUnitAttackPreDamageListenerPriority(int priority) {
		this.priority = priority;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	
}
