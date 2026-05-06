package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.attackmodifier.internal;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums.ABAttackTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;

public class ABCallbackGetModifiedAttackAttackType extends ABAttackTypeCallback {

	@Override
	public CAttackType callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.THEATTACK, castId), CUnitAttack.class)
				.getAttackType();
	}

}
