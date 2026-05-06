package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unitlisteners.sharedinternalcallbacks;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDamageCalculation;

/*
 * This can technically be called in any of:
 *  EvasionListener, PostDamageListener, PreDamageListener, DamageTakenListener, DamageTakenModificationListener
 *  
 *  It's really meant for DamageTaken or PostDamage, as in other cases may expect other changes to be made after
 */
public class ABCallbackGetFinalTotalDamageDealt extends ABFloatCallback {

	@Override
	public Float callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return (localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.DAMAGECALC, castId), CDamageCalculation.class))
				.computeFinalDamage(localStore.game, caster);
	}

}
