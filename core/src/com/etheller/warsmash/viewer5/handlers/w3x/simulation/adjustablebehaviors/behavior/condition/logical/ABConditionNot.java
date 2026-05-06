package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.logical;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABConditionNot extends ABBooleanCallback {

	private ABBooleanCallback condition;

	@Override
	public Boolean callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return !condition.callback(caster, localStore, castId);
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "not " + this.condition.generateJassEquivalent(jassTextGenerator);
	}

}
