package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast.AutocastType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackConditionalAutocastType extends ABAutocastTypeCallback {

	private ABBooleanCallback condition;
	private ABAutocastTypeCallback value1;
	private ABAutocastTypeCallback value2;

	@Override
	public AutocastType callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		if (condition.callback(caster, localStore, castId)) {
			return value1.callback(caster, localStore, castId);
		}
		return value2.callback(caster, localStore, castId);
	}

}
