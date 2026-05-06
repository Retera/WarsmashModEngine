package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetUnitFacing extends ABFloatCallback {

	private ABUnitCallback unit;

	@Override
	public Float callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return this.unit.callback(caster, localStore, castId).getFacing();
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "GetUnitFacing(" + this.unit.generateJassEquivalent(jassTextGenerator) + ")";
	}

}
