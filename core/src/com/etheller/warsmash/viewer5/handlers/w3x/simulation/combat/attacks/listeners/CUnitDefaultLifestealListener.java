package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;

public class CUnitDefaultLifestealListener implements CUnitAttackPostDamageListener{
	private float amount;
	
	public CUnitDefaultLifestealListener(float amount) {
		super();
		this.amount = amount;
	}

	public void onHit(final CSimulation simulation, CUnit attacker, AbilityTarget target, float damage) {
		attacker.heal(simulation, damage * this.amount);
	}

	public float getAmount() {
		return amount;
	}


	public void setAmount(float lifesteal) {
		this.amount = lifesteal;
	}
}
