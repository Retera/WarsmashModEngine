package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;

public class CUnitDefaultEtherealDamageModListener implements CUnitAttackDamageTakenModificationListener {
	public static CUnitDefaultEtherealDamageModListener INSTANCE = new CUnitDefaultEtherealDamageModListener();

	@Override
	public CUnitAttackDamageTakenModificationListenerDamageModResult onDamage(CSimulation game, CUnit attacker,
			CUnit target, boolean isAttack, boolean isRanged, CAttackType attackType, CDamageType damageType,
			CUnitAttackDamageTakenModificationListenerDamageModResult previousDamage) {
		boolean allied = game.getPlayer(attacker.getPlayerIndex()).hasAlliance(target.getPlayerIndex(), CAllianceType.PASSIVE);
		if (!allied || (allied && game.getGameplayConstants().isEtherealDamageBonusAlly())) {
			if (attackType == CAttackType.MAGIC) {
				previousDamage.addDamageMultiplier(game.getGameplayConstants().getEtherealDamageBonusMagic());
			}
			if (attackType == CAttackType.SPELLS) {
				previousDamage.addDamageMultiplier(game.getGameplayConstants().getEtherealDamageBonusSpells());
			}
		}
		if (damageType == CDamageType.NORMAL && attackType != CAttackType.MAGIC) {
			previousDamage.setBaseDamage(0);
			previousDamage.setBonusDamage(0);
			previousDamage.setDamageMultiplier(0);
		}
		
		return previousDamage;
	}
}
