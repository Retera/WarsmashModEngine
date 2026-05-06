package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unitlisteners.sharedinternalcallbacks;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums.ABAttackTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDamageCalculation;

/*
 * This can technically be called in any of:
 *  EvasionListener, PostDamageListener, PreDamageListener, DamageTakenListener, DamageTakenModificationListener
 *  
 */
public class ABCallbackGetTriggeringAttackType extends ABAttackTypeCallback {

	@Override
	public CAttackType callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return (localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.DAMAGECALC, castId), CDamageCalculation.class))
				.getAttackType();
	}

}
