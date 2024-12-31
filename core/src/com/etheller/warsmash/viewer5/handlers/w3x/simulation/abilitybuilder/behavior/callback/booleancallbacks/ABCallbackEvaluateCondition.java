package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;

public class ABCallbackEvaluateCondition extends ABBooleanCallback {
	
	private ABCondition condition;
	
	@Override
	public Boolean callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return condition.evaluate(game, caster, localStore, castId);
	}

}
