package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.numeric;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;

public class ABConditionFloatGt implements ABCondition {

	private ABFloatCallback value1;
	private ABFloatCallback value2;

	@Override
	public boolean evaluate(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		final Float v1 = this.value1.callback(game, caster, localStore, castId);
		final Float v2 = this.value2.callback(game, caster, localStore, castId);

		return v1 > v2;
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return this.value1.generateJassEquivalent(jassTextGenerator) + " > "
				+ this.value2.generateJassEquivalent(jassTextGenerator);
	}
}
