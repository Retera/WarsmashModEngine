package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class ABCallbackMaxFloat extends ABFloatCallback {

	private ABFloatCallback value1;
	private ABFloatCallback value2;

	@Override
	public Float callback(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		return Math.max(this.value1.callback(game, caster, localStore, castId),
				this.value2.callback(game, caster, localStore, castId));
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "RMaxBJ(" + this.value1.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.value2.generateJassEquivalent(jassTextGenerator) + ")";
	}

}
