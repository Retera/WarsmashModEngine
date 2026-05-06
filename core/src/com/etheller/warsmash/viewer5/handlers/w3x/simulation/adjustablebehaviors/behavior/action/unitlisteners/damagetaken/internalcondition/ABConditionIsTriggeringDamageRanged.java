package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unitlisteners.damagetaken.internalcondition;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDamageCalculation;

public class ABConditionIsTriggeringDamageRanged extends ABBooleanCallback {

	@Override
	public Boolean callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return (localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.DAMAGECALC, castId), CDamageCalculation.class))
				.getPrimaryDamageFlags().isRanged();
	}

}
