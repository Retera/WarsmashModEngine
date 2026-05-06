package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDamageCalculation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public interface CUnitAttackDamageTakenListener {
	public static final EnumSet<CTargetType> ENEMY_TARGET = EnumSet.of(CTargetType.ENEMIES);
	
	public int getPriority(CSimulation simulation, CUnit target, CDamageCalculation damage);
	
	public void onDamage(CSimulation simulation, CUnit target, CDamageCalculation damage);
}
