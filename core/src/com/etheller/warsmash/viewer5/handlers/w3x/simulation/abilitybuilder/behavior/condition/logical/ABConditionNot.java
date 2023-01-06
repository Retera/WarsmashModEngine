package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.logical;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;

public class ABConditionNot implements ABCondition {

	private ABCondition condition;
	
	@Override
	public boolean evaluate(CSimulation game, CUnit caster, Map<String, Object> localStore) {
		return !condition.evaluate(game, caster, localStore);
	}

}
