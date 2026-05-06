package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDamageCalculation;

public interface CUnitAttackDamageTakenModificationListener {
	public int getPriority(CSimulation simulation, CUnit cUnit, CDamageCalculation damage);
	
	public void onDamage(CSimulation simulation, CUnit cUnit, CDamageCalculation damage);
}
