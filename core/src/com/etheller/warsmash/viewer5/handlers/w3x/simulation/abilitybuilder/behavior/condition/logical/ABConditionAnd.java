package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.logical;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;

public class ABConditionAnd extends ABCondition {

	private List<ABCondition> conditions;
	
	@Override
	public Boolean callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		boolean result = true;
		if (conditions != null) {
			for (ABCondition cond : conditions) {
				if (result) {
					result = result && cond.callback(game, caster, localStore, castId);
				}
			}
		}
		return result;
	}

}
