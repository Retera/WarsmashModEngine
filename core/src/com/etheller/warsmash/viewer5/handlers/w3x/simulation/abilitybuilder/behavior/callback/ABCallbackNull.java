package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCallback;

public class ABCallbackNull implements ABCallback {


	@Override
	public Object callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return null;
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "JASSTODO";
	}

}
