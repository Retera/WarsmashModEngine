package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackFloorFloat extends ABFloatCallback {

	private ABFloatCallback value;
	
	@Override
	public Float callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return (float) Math.floor(value.callback(caster, localStore, castId));
	}

}
