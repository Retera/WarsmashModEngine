package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackIdToString extends ABStringCallback {
	
	private ABIDCallback value;
	
	@Override
	public String callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return value.callback(caster, localStore, castId).asStringValue();
	}

}
