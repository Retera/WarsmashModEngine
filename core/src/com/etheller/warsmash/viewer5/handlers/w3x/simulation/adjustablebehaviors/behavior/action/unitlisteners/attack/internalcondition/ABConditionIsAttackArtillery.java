package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unitlisteners.attack.internalcondition;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissileLine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissileSplash;

public class ABConditionIsAttackArtillery extends ABBooleanCallback {

	@Override
	public Boolean callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		CUnitAttack attack = localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.THEATTACK, castId),
				CUnitAttack.class);
		if (attack instanceof CUnitAttackMissileSplash) {
			return ((CUnitAttackMissileSplash) attack).isArtillery();
		}
		if (attack instanceof CUnitAttackMissileLine) {
			return ((CUnitAttackMissileLine) attack).isArtillery();
		}
		return false;
	}

}
