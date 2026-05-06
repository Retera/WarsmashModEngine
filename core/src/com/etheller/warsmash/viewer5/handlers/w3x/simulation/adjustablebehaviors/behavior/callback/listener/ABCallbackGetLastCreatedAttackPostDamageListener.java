package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.listener;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.listener.ABAttackPostDamageListener;

public class ABCallbackGetLastCreatedAttackPostDamageListener extends ABAttackPostDamageListenerCallback {

	@Override
	public ABAttackPostDamageListener callback(CUnit caster, ABLocalDataStore localStore,
			final int castId) {
		return localStore.get(ABLocalStoreKeys.LASTCREATEDAPoDL, ABAttackPostDamageListener.class);
	}

}
