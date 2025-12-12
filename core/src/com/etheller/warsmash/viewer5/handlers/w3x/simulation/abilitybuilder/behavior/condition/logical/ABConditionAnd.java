package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.logical;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
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

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		String result = "";
		for (int i = 0 ; i < conditions.size() ; i++) {
			result += conditions.get(i).generateJassEquivalent(jassTextGenerator);
			if (i < (conditions.size() - 1)) {
				result += " and ";
			}
		}
		return result;
	}

}
