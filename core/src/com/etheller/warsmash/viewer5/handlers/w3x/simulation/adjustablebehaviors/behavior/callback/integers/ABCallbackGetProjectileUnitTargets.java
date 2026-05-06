package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.parsers.jass.JassTextGeneratorType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABCallbackGetProjectileUnitTargets extends ABIntegerCallback {

	@Override
	public Integer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.PROJECTILEUNITTARGETS, castId), Integer.class);
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return jassTextGenerator.getUserDataExpr(
				"AB_LOCAL_STORE_KEY_PROJECTILEUNITTARGETS + I2S(" + jassTextGenerator.getCastId() + ")",
				JassTextGeneratorType.Integer);
	}

}
