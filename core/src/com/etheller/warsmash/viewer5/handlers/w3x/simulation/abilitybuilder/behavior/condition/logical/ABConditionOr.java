package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.logical;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;

public class ABConditionOr implements ABCondition {

	private ABCondition condition1;
	private ABCondition condition2;
	
	@Override
	public boolean evaluate(CSimulation game, CUnit caster, Map<String, Object> localStore) {
		return condition1.evaluate(game, caster, localStore) || condition2.evaluate(game, caster, localStore);
	}

}
