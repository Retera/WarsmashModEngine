package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.numeric;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;

public class ABConditionFloatLt implements ABCondition {

	private ABFloatCallback value1;
	private ABFloatCallback value2;
	
	@Override
	public boolean evaluate(CSimulation game, CUnit caster, Map<String, Object> localStore) {
		Float v1 = value1.callback(game, caster, localStore);
		Float v2 = value2.callback(game, caster, localStore);
		
		return v1<v2;	
	}
}
