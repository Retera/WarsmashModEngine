package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.booleans;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackInlineConditionBoolean extends ABBooleanCallback {

	private ABBooleanCallback condition;
	private ABBooleanCallback pass;
	private ABBooleanCallback fail;

	@Override
	public Boolean callback(CUnit caster, ABLocalDataStore localStore, int castBoolean) {
		if (condition != null && condition.callback(caster, localStore, castBoolean)) {
			return pass.callback(caster, localStore, castBoolean);
		}
		return fail.callback(caster, localStore, castBoolean);
	}

}
