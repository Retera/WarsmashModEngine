package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;

public class CUnitDefaultMagicImmuneDamageModListener implements CUnitAttackFinalDamageTakenModificationListener {
	public static CUnitDefaultMagicImmuneDamageModListener INSTANCE = new CUnitDefaultMagicImmuneDamageModListener();

	@Override
	public float onDamage(CSimulation game, CUnit attacker, CUnit target, boolean isAttack, boolean isRanged,
			CAttackType attackType, CDamageType damageType, float previousDamage) {
		if ((damageType != CDamageType.NORMAL && (damageType != CDamageType.UNIVERSAL && game.getGameplayConstants().isMagicImmuneResistsUltimates())
				&& attackType == CAttackType.SPELLS)
				|| (attackType == CAttackType.MAGIC && game.getGameplayConstants().isMagicImmuneResistsDamage())) {
			return 0;
		}

		return previousDamage;
	}
}
