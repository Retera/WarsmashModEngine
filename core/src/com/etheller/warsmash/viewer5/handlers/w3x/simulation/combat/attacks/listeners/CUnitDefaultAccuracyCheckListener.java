package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import java.util.Random;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;

public class CUnitDefaultAccuracyCheckListener implements CUnitAttackPreDamageListener{
	
	public CUnitDefaultAccuracyCheckListener() {
		super();
	}

	@Override
	public CUnitAttackEffectListenerStacking onAttack(CSimulation simulation, CUnit attacker, AbilityTarget target, CWeaponType weaponType, CAttackType attackType, CDamageType damageType,
			CUnitAttackPreDamageListenerDamageModResult damageResult) {
		boolean miss = false;
		if (target instanceof CUnit) {
			if (simulation.getTerrainHeight(attacker.getX(), attacker.getY()) < simulation.getTerrainHeight(target.getX(), target.getY())) {
				Random random = simulation.getSeededRandom();
				if (random.nextFloat(1f) < simulation.getGameplayConstants().getChanceToMiss()) {
					miss = true;
				}
			}
			miss = miss || ((CUnit) target).checkForMiss(simulation, attacker, true, miss, null, null, damageResult.getBaseDamage(), damageResult.getBonusDamage());
			
		}
		if (miss) {
			damageResult.setMiss(true);
			return new CUnitAttackEffectListenerStacking(false, true);
		}
		return new CUnitAttackEffectListenerStacking();
	}
}
