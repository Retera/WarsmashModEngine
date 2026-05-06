package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.longs;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackMaxLong extends ABLongCallback {

	private ABLongCallback value1;
	private ABLongCallback value2;

	@Override
	public Long callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return Math.max(value1.callback(caster, localStore, castId),
				value2.callback(caster, localStore, castId));
	}

}
