package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement.CUnitPriorityLoopData;

public interface CUnitAttackPostDamageListener {
	public void onHit(final CSimulation simulation, CUnit attacker, AbilityTarget target, CUnitAttack cUnitAttack, float damage, CUnitPriorityLoopData postListenerLoop);

	public int getPriority(CSimulation simulation, CUnit attacker, AbilityTarget target, CUnitAttack cUnitAttack);
}
