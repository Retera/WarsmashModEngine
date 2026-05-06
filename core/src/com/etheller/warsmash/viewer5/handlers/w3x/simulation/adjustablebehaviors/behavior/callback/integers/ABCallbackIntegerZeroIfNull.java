package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackIntegerZeroIfNull extends ABIntegerCallback {

	private ABIntegerCallback value;

	@Override
	public Integer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		Integer v = value.callback(caster, localStore, castId);
		return v == null ? 0 : v;
	}

}
