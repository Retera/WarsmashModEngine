package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class ABCallbackNegativeFloat extends ABFloatCallback {

	private ABFloatCallback value;

	@Override
	public Float callback(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		return -1 * this.value.callback(game, caster, localStore, castId);
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "-(" + this.value.generateJassEquivalent(jassTextGenerator) + ")";
	}

}
