package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackTruncateFloatToInteger extends ABIntegerCallback {

	private ABFloatCallback value;

	@Override
	public Integer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return value.callback(caster, localStore, castId).intValue();
	}

}
