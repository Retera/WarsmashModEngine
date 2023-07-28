package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CWeaponType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.TextTagConfigType;

public class CUnitDefaultAccuracyCheckListener implements CUnitAttackPreDamageListener{
	
	public CUnitDefaultAccuracyCheckListener() {
		super();
	}

	@Override
	public CUnitAttackEffectListenerStacking onAttack(CSimulation simulation, CUnit attacker, AbilityTarget target, CWeaponType weaponType, CAttackType attackType, CDamageType damageType,
			CUnitAttackPreDamageListenerDamageModResult damageResult) {
		boolean miss = false;
		if (target instanceof CUnit) {
			//if (attacker.getTerrainHeight() < target.getTerrainHeight()) {
			//	Random random = simulation.getSeededRandom();
			//	if (random.nextFloat(1f) > simulation.getGameplayConstants().getChanceToMiss()) {
			//		miss = true;
			//	}
			//}
			miss = miss || ((CUnit) target).checkForMiss(simulation, attacker, true, miss, null, null, damageResult.getBaseDamage(), damageResult.getBonusDamage());
			
		}
		if (miss) {
			if (weaponType == CWeaponType.MSPLASH || weaponType == CWeaponType.ARTILLERY) {
				damageResult.setDamageMultiplier(simulation.getGameplayConstants().getMissDamageReduction());
				
			} else {
				damageResult.setBaseDamage(0);
				damageResult.setBonusDamage(0);
				damageResult.setDamageMultiplier(0);
				simulation.spawnTextTag(attacker, attacker.getPlayerIndex(), TextTagConfigType.MISS_TEXT, 0);
				
			}
			return new CUnitAttackEffectListenerStacking(false, false);
		}
		return new CUnitAttackEffectListenerStacking();
	}
}
