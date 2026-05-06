package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.logical;

import java.util.List;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABConditionAnd extends ABBooleanCallback {

	private List<ABBooleanCallback> conditions;

	@Override
	public Boolean callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		boolean result = true;
		if (conditions != null) {
			for (ABBooleanCallback cond : conditions) {
				if (result) {
					result = result && cond.callback(caster, localStore, castId);
				}
			}
		}
		return result;
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		String result = "";
		for (int i = 0; i < conditions.size(); i++) {
			result += conditions.get(i).generateJassEquivalent(jassTextGenerator);
			if (i < (conditions.size() - 1)) {
				result += " and ";
			}
		}
		return result;
	}

}
