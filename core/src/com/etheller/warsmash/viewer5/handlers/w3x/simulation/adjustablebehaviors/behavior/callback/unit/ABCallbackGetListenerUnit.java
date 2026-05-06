package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetListenerUnit extends ABUnitCallback {

	@Override
	public CUnit callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return caster;
	}

}
