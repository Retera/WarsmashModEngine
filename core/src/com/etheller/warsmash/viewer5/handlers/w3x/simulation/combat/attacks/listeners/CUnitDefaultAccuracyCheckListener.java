package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import java.util.Random;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDamageCalculation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.replacement.CUnitAttackSettings;

public class CUnitDefaultAccuracyCheckListener implements CUnitAttackPreDamageListener {

	public CUnitDefaultAccuracyCheckListener() {
		super();
	}

	@Override
	public void onAttack(CSimulation simulation, AbilityTarget target,
			AbilityPointTarget attackImpactLocation, CUnitAttack attack, CUnitAttackSettings settings,
			CDamageCalculation damageResult) {
		boolean miss = false;
		if (target instanceof CUnit && damageResult.getSource() != null) {
			if (simulation.getTerrainHeight(damageResult.getSource().getX(), damageResult.getSource().getY()) < simulation
					.getTerrainHeight(target.getX(), target.getY())) {
				Random random = simulation.getSeededRandom();
				if (random.nextFloat(1f) < simulation.getGameplayConstants().getChanceToMiss()) {
					miss = true;
				}
			}
			miss = miss || ((CUnit) target).checkForMiss(simulation, damageResult);
		}
		if (miss) {
			damageResult.setMiss(true);
			if (!settings.isApplyEffectsOnMiss()) {
				damageResult.preventOtherModificationsOfOtherPriorities();
				return;
			}
		}
	}
}
