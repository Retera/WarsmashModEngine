package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackRawString extends ABStringCallback {

	private String value;

	@Override
	public String callback(final CUnit caster, final ABLocalDataStore localStore,
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
