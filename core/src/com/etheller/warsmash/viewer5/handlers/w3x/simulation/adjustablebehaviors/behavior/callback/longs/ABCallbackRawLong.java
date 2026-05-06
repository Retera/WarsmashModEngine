package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.longs;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackRawLong extends ABLongCallback {

	private Long value;

	@Override
	public Long callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return value;
	}

}
