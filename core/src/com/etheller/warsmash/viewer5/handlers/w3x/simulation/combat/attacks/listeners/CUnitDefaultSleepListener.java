package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuffType;

public class CUnitDefaultSleepListener implements CUnitAttackDamageTakenListener {
	public static CUnitDefaultSleepListener INSTANCE = new CUnitDefaultSleepListener();
	
	public CUnitDefaultSleepListener () {
	}
	
	@Override
	public void onDamage(final CSimulation simulation, CUnit attacker, CUnit target, boolean isAttack, boolean isRanged, CDamageType damageType, float damage, float bonusDamage, float trueDamage) {
		target.removeAllStateModBuffs(StateModBuffType.SLEEPING);
		target.computeUnitState(simulation, StateModBuffType.SLEEPING);
	}

}
