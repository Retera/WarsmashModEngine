package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackMinFloat extends ABFloatCallback {

	private ABFloatCallback value1;
	private ABFloatCallback value2;

	@Override
	public Float callback(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		return Math.min(this.value1.callback(caster, localStore, castId),
				this.value2.callback(caster, localStore, castId));
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "RMinBJ(" + this.value1.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.value2.generateJassEquivalent(jassTextGenerator) + ")";
	}

}
