package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackIntegerZeroIfFalse extends ABIntegerCallback {

	private ABIntegerCallback value;
	private ABBooleanCallback bool;
	
	@Override
	public Integer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		if (bool == null || !bool.callback(caster, localStore, castId)) {
			return 0;
		}
		return value.callback(caster, localStore, castId);
	}

}
