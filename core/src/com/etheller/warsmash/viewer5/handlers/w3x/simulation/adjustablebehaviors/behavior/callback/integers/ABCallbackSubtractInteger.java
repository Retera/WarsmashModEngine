package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackSubtractInteger extends ABIntegerCallback {

	private ABIntegerCallback value1;
	private ABIntegerCallback value2;
	
	@Override
	public Integer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return value1.callback(caster, localStore, castId) - value2.callback(caster, localStore, castId);
	}

}
