package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.logical;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;

public class ABConditionAnd implements ABCondition {

	private List<ABCondition> conditions;
	
	@Override
	public boolean evaluate(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		boolean result = true;
		if (conditions != null) {
			for (ABCondition cond : conditions) {
				if (result) {
					result = result && cond.evaluate(game, caster, localStore, castId);
				}
			}
		}
		return result;
	}

}
