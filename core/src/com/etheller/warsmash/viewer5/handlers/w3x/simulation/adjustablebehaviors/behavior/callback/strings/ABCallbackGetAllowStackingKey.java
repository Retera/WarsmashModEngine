package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;

public class ABCallbackGetAllowStackingKey extends ABStringCallback {

	@Override
	public String callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return NonStackingStatBuff.ALLOW_STACKING_KEY;
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "\"" + NonStackingStatBuff.ALLOW_STACKING_KEY + "\"";
	}

}
