package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackIntToFloat extends ABFloatCallback {

	private ABIntegerCallback value;

	@Override
	public Float callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return this.value.callback(caster, localStore, castId).floatValue();
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return this.value.generateJassEquivalent(jassTextGenerator);
	}

}
