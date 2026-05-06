package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.orderid;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIdUtils;

public class ABCallbackIdString extends ABOrderIdCallback {
	
	private ABStringCallback idString;
	
	@Override
	public Integer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return OrderIdUtils.getOrderId(idString.callback(caster, localStore, castId));
	}

}
