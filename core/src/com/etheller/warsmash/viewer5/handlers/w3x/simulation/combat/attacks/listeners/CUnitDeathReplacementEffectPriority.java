package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

public enum CUnitDeathReplacementEffectPriority {
	PHOENIXREVIVE(0),
	ABILITYREINCARNATION(1),
	ITEMREINCARNATION(2);

	private int priority;
	
	CUnitDeathReplacementEffectPriority(int priority) {
		this.priority = priority;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	
}
