package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.booleans;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackIntegerToBoolean extends ABBooleanCallback {

	private ABIntegerCallback value;

	@Override
	public Boolean callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return this.value.callback(caster, localStore, castId) != 0;
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return this.value.generateJassEquivalent(jassTextGenerator) + " != 0";
	}

}
