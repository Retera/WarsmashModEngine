package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackRawInteger extends ABIntegerCallback {

	private Integer value;

	@Override
	public Integer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return this.value;
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return this.value.toString();
	}

}
