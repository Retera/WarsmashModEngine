package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.buff.ABBuffCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetBuffHandleAsString extends ABStringCallback {
	
	private ABBuffCallback buff;
	
	@Override
	public String callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return "" + buff.callback(caster, localStore, castId).getHandleId();
	}

}
