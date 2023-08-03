package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import java.util.EnumSet;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;

public interface CUnitAttackDamageTakenListener {
	public static final EnumSet<CTargetType> ENEMY_TARGET = EnumSet.of(CTargetType.ENEMIES);
	
	public void onDamage(final CSimulation simulation, CUnit attacker, CUnit target, boolean isAttack, boolean isRanged, CDamageType weaponType, float damage, float bonusDamage, float trueDamage);
}
