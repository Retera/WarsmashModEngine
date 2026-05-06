package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.structural;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABActionResetPeriodicExecute implements ABAction {

	private ABCallback unique;

	@Override
	public void runAction(CUnit caster, ABLocalDataStore localStore, int castId) {
		if (this.unique != null) {
			localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PERIODICNEXTTICK, castId) + "$"
					+ this.unique.callback(caster, localStore, castId));
		} else {
			localStore.remove(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PERIODICNEXTTICK, castId));
		}
	}

}
