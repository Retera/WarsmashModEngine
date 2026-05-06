package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unitqueue;

import java.util.Queue;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABCallbackGetLastCreatedUnitQueue extends ABUnitQueueCallback {

	@SuppressWarnings("unchecked")
	@Override
	public Queue<CUnit> callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return (Queue<CUnit>) localStore.get(ABLocalStoreKeys.LASTCREATEDUNITQUEUE);
	}

}
