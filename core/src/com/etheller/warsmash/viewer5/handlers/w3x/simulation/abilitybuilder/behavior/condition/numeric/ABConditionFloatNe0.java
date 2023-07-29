package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.numeric;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;

public class ABConditionFloatNe0 implements ABCondition {

	private ABFloatCallback value;
	
	@Override
	public boolean evaluate(CSimulation game, CUnit caster, Map<String, Object> localStore) {
		Float v = value.callback(game, caster, localStore);

		System.err.println("Checking value for non-zero: " + v + " and got " + (v!=0));
		return v!=0;
	}

}
