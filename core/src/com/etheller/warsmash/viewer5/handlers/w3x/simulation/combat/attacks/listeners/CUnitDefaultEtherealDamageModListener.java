package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDamageCalculation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;

public class CUnitDefaultEtherealDamageModListener implements CUnitAttackDamageTakenModificationListener {
	public static CUnitDefaultEtherealDamageModListener INSTANCE = new CUnitDefaultEtherealDamageModListener();

	@Override
	public int getPriority(CSimulation simulation, CUnit target, CDamageCalculation damage) {
		return 0;
	}

	@Override
	public void onDamage(CSimulation game, CUnit target, CDamageCalculation damage) {
		if (damage.getSource() == null || game.getGameplayConstants().isEtherealDamageBonusAlly() || !game
				.getPlayer(damage.getSource().getPlayerIndex()).hasAlliance(target.getPlayerIndex(), CAllianceType.PASSIVE)) {
			damage.addDamageMultiplier(game.getGameplayConstants().getEtherealDamageBonus()[damage.getAttackType().ordinal()]);
		}
	}
}
