package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetUnitAcquisitionRange extends ABFloatCallback {

	private ABUnitCallback unit;
	
	@Override
	public Float callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		CUnit theUnit = unit.callback(caster, localStore, castId);
		if (theUnit.getAcquisitionRange() > 0) {
			return theUnit.getAcquisitionRange();
		}
		return (float) theUnit.getUnitType().getSightRadiusDay();
	}

}
