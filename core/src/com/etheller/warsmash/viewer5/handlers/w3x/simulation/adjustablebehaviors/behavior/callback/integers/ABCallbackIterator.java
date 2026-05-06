package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABCallbackIterator extends ABIntegerCallback {

	private ABCallback unique;

	@Override
	public Integer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		if (this.unique != null) {
			return localStore.get(ABLocalStoreKeys.combineKey(
					ABLocalStoreKeys.ITERATORCOUNT + "$" + this.unique.callback(caster, localStore, castId), castId),
					Integer.class);
		} else {
			return localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ITERATORCOUNT, castId), Integer.class);
		}
	}

}
