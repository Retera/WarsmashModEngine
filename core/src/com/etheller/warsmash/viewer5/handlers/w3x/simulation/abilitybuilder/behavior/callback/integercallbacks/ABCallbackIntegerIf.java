package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;

public class ABCallbackIntegerIf extends ABIntegerCallback {

	private ABIntegerCallback value1;
	private ABIntegerCallback value2;
	private ABCondition condition;
	
	@Override
	public Integer callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		if (condition == null || !condition.evaluate(game, caster, localStore, castId)) {
			return value2.callback(game, caster, localStore, castId);
		}
		return value1.callback(game, caster, localStore, castId);
	}

}
