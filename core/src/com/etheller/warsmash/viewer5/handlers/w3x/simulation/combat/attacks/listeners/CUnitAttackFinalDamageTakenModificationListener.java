package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;

public interface CUnitAttackFinalDamageTakenModificationListener {
	public float onDamage(final CSimulation simulation, CUnit attacker, CUnit target, boolean isAttack, boolean isRanged, CAttackType attackType, CDamageType damageType, float previousDamage);
}
