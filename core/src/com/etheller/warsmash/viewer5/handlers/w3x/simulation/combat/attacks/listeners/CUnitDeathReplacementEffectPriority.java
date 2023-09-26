package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

public enum CUnitDeathReplacementEffectPriority {
	PHOENIXREVIVE(0),
	GENERALONDEATHACTIONS(1),
	ABILITYREINCARNATION(2),
	ITEMREINCARNATION(3);

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
