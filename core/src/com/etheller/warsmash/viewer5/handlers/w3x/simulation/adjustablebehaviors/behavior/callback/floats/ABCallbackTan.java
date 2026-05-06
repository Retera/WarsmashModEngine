package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackTan extends ABFloatCallback {

	private ABFloatCallback angle;

	@Override
	public Float callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return (float) StrictMath.tan(angle.callback(caster, localStore, castId));
	}

}
