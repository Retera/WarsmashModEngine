package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.behavior;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABActionAttemptToResumePreviousBehavior implements ABAction {

	private ABBooleanCallback checkForOrders;
	
	@Override
	public void runAction(CUnit caster, ABLocalDataStore localStore, int castId) {
		if (checkForOrders == null || checkForOrders.callback(caster, localStore, castId)) {
			if (caster.getOrderQueue().isEmpty()) {
				localStore.put(ABLocalStoreKeys.NEWBEHAVIOR, localStore.get(ABLocalStoreKeys.PREVIOUSBEHAVIOR));
			}
		} else {
			localStore.put(ABLocalStoreKeys.NEWBEHAVIOR, localStore.get(ABLocalStoreKeys.PREVIOUSBEHAVIOR));
		}
	}

}
