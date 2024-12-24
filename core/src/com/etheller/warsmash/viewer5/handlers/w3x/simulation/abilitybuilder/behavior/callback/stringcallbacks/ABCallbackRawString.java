package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class ABCallbackRawString extends ABStringCallback {

	private String value;

	@Override
	public String callback(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		return this.value;
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "\"" + this.value + "\"";
	}

	public String getValue() {
		return this.value;
	}
}
