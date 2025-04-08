package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unitlisteners.internalConditions;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissileLine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissileSplash;

public class ABConditionIsAttackArtillery extends ABCondition {
	
	@Override
	public Boolean callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		CUnitAttack attack = (CUnitAttack) localStore.get(ABLocalStoreKeys.THEATTACK+castId);
		if (attack instanceof CUnitAttackMissileSplash) {
			return ((CUnitAttackMissileSplash)attack).isArtillery();
		}
		if (attack instanceof CUnitAttackMissileLine) {
			return ((CUnitAttackMissileLine)attack).isArtillery();
		}
		return false;
	}

}
