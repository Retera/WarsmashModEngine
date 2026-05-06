package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.location;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.parsers.jass.JassTextGeneratorType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABCallbackGetTargetedLocation extends ABLocationCallback {

	@Override
	public AbilityPointTarget callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		final AbilityPointTarget target = localStore.get(
				ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDLOCATION, castId),
				AbilityPointTarget.class);

		return target;
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return jassTextGenerator.getUserDataExpr(
				"AB_LOCAL_STORE_KEY_ABILITYTARGETEDLOCATION + I2S(" + jassTextGenerator.getCastId() + ")",
				JassTextGeneratorType.LocationHandle);
	}

}
