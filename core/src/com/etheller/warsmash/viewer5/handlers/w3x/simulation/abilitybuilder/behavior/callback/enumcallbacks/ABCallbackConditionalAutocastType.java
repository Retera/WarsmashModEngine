package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast.AutocastType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;

public class ABCallbackConditionalAutocastType extends ABAutocastTypeCallback {

	private ABCondition condition;
	private ABAutocastTypeCallback value1;
	private ABAutocastTypeCallback value2;
	
	@Override
	public AutocastType callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		if (condition.evaluate(game, caster, localStore, castId)) {
			return value1.callback(game, caster, localStore, castId);
		}
		return value2.callback(game, caster, localStore, castId);
	}

}
