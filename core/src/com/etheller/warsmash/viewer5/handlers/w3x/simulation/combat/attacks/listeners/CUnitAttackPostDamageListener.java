package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDamageCalculation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;

public interface CUnitAttackPostDamageListener {
	public int getPriority(CSimulation simulation, CUnit attacker, AbilityTarget target, CUnitAttack cUnitAttack);

	public void onHit(CSimulation simulation, AbilityTarget target, CUnitAttack cUnitAttack, CDamageCalculation damage);
}
