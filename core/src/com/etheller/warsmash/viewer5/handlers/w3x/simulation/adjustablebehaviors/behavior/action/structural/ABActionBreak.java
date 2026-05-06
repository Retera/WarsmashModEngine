package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.structural;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.parsers.jass.JassTextGeneratorType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABActionBreak implements ABSingleAction {

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		localStore.put(ABLocalStoreKeys.BREAK, true);
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return jassTextGenerator.setUserDataExpr("AB_LOCAL_STORE_KEY_BREAK", JassTextGeneratorType.Boolean, "true");
	}
}
