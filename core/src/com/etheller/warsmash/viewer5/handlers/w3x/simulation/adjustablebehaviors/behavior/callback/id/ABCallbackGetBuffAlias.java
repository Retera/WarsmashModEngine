package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.buff.ABBuffCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetBuffAlias extends ABIDCallback {
	
	private ABBuffCallback buff;
	
	@Override
	public War3ID callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return buff.callback(caster, localStore, castId).getAlias();
	}

}
