package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unitqueue.ABUnitQueueCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetUnitQueueSize extends ABIntegerCallback {

	private ABUnitQueueCallback queue;

	@Override
	public Integer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return queue.callback(caster, localStore, castId).size();
	}

}
