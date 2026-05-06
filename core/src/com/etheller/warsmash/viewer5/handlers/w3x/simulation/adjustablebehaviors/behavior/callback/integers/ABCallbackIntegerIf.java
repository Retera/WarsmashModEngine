package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackIntegerIf extends ABIntegerCallback {

	private ABIntegerCallback value1;
	private ABIntegerCallback value2;
	private ABBooleanCallback condition;
	
	@Override
	public Integer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		if (condition == null || !condition.callback(caster, localStore, castId)) {
			return value2.callback(caster, localStore, castId);
		}
		return value1.callback(caster, localStore, castId);
	}

}
