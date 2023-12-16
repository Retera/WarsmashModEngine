package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.numeric;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;

public class ABConditionIntegerEq0 implements ABCondition {

	private ABIntegerCallback value;
	
	@Override
	public boolean evaluate(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		Integer v1 = value.callback(game, caster, localStore, castId);
		return v1 == 0;
	}

}
