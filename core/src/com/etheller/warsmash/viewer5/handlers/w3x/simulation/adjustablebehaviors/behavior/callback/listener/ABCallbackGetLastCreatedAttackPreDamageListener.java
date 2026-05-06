package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.listener;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.listener.ABAttackPreDamageListener;

public class ABCallbackGetLastCreatedAttackPreDamageListener extends ABAttackPreDamageListenerCallback {

	@Override
	public ABAttackPreDamageListener callback(CUnit caster, ABLocalDataStore localStore,
			final int castId) {
		return localStore.get(ABLocalStoreKeys.LASTCREATEDAPrDL, ABAttackPreDamageListener.class);
	}

}
