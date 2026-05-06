package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.logical;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABConditionOr extends ABBooleanCallback {

	private List<ABBooleanCallback> conditions;

	@Override
	public Boolean callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		boolean result = false;
		if (conditions != null) {
			for (ABBooleanCallback cond : conditions) {
				if (!result) {
					result = result || cond.callback(caster, localStore, castId);
				}
			}
		}
		return result;
	}

}
