package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;

public class CUnitDefaultEtherealDamageModListener implements CUnitAttackDamageTakenModificationListener {
	public static CUnitDefaultEtherealDamageModListener INSTANCE = new CUnitDefaultEtherealDamageModListener();

	@Override
	public CUnitAttackDamageTakenModificationListenerDamageModResult onDamage(CSimulation simulation, CUnit attacker,
			CUnit target, boolean isAttack, boolean isRanged, CAttackType attackType, CDamageType damageType,
			CUnitAttackDamageTakenModificationListenerDamageModResult previousDamage) {
		if (attackType == CAttackType.MAGIC || attackType == CAttackType.SPELLS) {
			previousDamage.addDamageMultiplier(1.75f);
		}
		return previousDamage;
	}
}
