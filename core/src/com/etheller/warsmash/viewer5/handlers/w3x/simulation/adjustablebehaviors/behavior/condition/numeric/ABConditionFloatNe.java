package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.numeric;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABConditionFloatNe extends ABBooleanCallback {

	private ABFloatCallback value1;
	private ABFloatCallback value2;
	
	@Override
	public Boolean callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		Float v1 = value1.callback(caster, localStore, castId);
		Float v2 = value2.callback(caster, localStore, castId);
		
		return !v1.equals(v2);
	}

}
