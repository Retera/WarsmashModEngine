package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDamageCalculation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuffType;

public class CUnitDefaultSleepListener implements CUnitAttackDamageTakenListener {
	public static CUnitDefaultSleepListener INSTANCE = new CUnitDefaultSleepListener();

	public CUnitDefaultSleepListener() {
	}

	@Override
	public int getPriority(CSimulation simulation, CUnit target, CDamageCalculation damage) {
		return 0;
	}

	@Override
	public void onDamage(CSimulation simulation, CUnit target, CDamageCalculation damage) {
		if (damage.computeFinalDamage(simulation, target) > 0) {
			target.removeAllStateModBuffs(StateModBuffType.SLEEPING);
			target.computeUnitState(simulation, StateModBuffType.SLEEPING);
		}
	}

}
