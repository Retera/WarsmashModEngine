package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABCallbackGetStoredFloatByKey extends ABFloatCallback {

	private ABStringCallback key;
	private ABBooleanCallback instanceValue;

	@Override
	public Float callback(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		if ((this.instanceValue == null) || this.instanceValue.callback(caster, localStore, castId)) {
			return localStore.get(
					ABLocalStoreKeys.combineUserInstanceKey(this.key.callback(caster, localStore, castId), castId),
					Float.class);
		} else {
			return localStore.get(
					ABLocalStoreKeys.combineUserKey(this.key.callback(caster, localStore, castId), castId),
					Float.class);
		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		if (this.instanceValue == null) {
			return "GetLocalStoreUserCastRealAU(" + jassTextGenerator.getTriggerLocalStore() + ", "
					+ this.key.generateJassEquivalent(jassTextGenerator) + ", " + jassTextGenerator.getCastId() + ")";
		}
		return "GetStoredRealAU(" + jassTextGenerator.getTriggerLocalStore() + ", "
				+ this.key.generateJassEquivalent(jassTextGenerator) + ", " + jassTextGenerator.getCastId() + ", "
				+ this.instanceValue.generateJassEquivalent(jassTextGenerator) + ")";
	}

}
