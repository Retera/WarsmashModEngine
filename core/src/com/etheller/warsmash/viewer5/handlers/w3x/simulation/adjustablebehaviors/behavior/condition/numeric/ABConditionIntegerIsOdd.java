package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.numeric;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABConditionIntegerIsOdd extends ABBooleanCallback {

	private ABIntegerCallback value;

	@Override
	public Boolean callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		Integer v1 = value.callback(caster, localStore, castId);

		return (v1 % 2) == 1;
	}

}
