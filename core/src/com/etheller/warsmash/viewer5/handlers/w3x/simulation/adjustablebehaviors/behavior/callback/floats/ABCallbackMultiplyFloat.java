package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackMultiplyFloat extends ABFloatCallback {

	private ABFloatCallback value1;
	private ABFloatCallback value2;

	@Override
	public Float callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return value1.callback(caster, localStore, castId) * value2.callback(caster, localStore, castId);
	}

}
