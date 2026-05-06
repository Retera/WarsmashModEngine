package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetDistanceBetweenUnits extends ABFloatCallback {

	private ABUnitCallback origin;
	private ABUnitCallback target;
	
	@Override
	public Float callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		CUnit o = origin.callback(caster, localStore, castId);
		CUnit t = target.callback(caster, localStore, castId);
		
		return (float) o.distance(t);
	}

}
