package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetUnitHandleAsString extends ABStringCallback {
	
	private ABUnitCallback unit;
	
	@Override
	public String callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return "" + unit.callback(caster, localStore, castId).getHandleId();
	}

}
