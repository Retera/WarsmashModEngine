package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.logical;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;

public class ABConditionAnd implements ABCondition {

	private ABCondition condition1;
	private ABCondition condition2;

	@Override
	public boolean evaluate(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return this.condition1.evaluate(game, caster, localStore, castId)
				&& this.condition2.evaluate(game, caster, localStore, castId);
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return this.condition1.generateJassEquivalent(jassTextGenerator) + " and "
				+ this.condition2.generateJassEquivalent(jassTextGenerator);
	}

}
