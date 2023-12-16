package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

public enum CUnitAttackPreDamageListenerPriority {
	WINDWALK(0),
	ACCURACY(1),
	CRITBASH(2),
	MASKPLUS(3),
	ORBSLOT1(4),
	ORBSLOT2(5),
	ORBSLOT3(6),
	ORBSLOT4(7),
	ORBSLOT5(8),
	ORBSLOT6(9),
	FEEDBACK(10);

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
