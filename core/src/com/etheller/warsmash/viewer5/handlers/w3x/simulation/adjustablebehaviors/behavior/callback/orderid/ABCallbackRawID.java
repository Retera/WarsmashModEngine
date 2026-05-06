package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.orderid;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackRawID extends ABOrderIdCallback {

	private Integer id;

	@Override
	public Integer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return id;
	}

}
