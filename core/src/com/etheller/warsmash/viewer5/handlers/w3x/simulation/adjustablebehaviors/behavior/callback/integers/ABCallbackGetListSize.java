package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.list.ABListCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetListSize extends ABIntegerCallback {

	private ABListCallback<?> list;

	@Override
	public Integer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return list.callback(caster, localStore, castId).size();
	}

}
