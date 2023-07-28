package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;

public interface CUnitAttackDamageTakenListener {
	public void onDamage(final CSimulation simulation, CUnit attacker, CUnit target, boolean isAttack, boolean isRanged, CDamageType weaponType, float damage, float bonusDamage, float trueDamage);
}
