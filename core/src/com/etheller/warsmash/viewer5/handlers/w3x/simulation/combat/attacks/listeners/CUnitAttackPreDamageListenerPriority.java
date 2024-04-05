package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

public enum CUnitAttackPreDamageListenerPriority {
	WINDWALK(0),
	ACCURACY(1),
	STACKING(2),
	CRITBASH(3),
	MASKPLUS(4),
	ORBSLOT1(5),
	ORBSLOT2(6),
	ORBSLOT3(7),
	ORBSLOT4(8),
	ORBSLOT5(9),
	ORBSLOT6(10),
	FEEDBACK(11);

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
