package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABCallbackGetStoredIntegerByKey extends ABIntegerCallback {

	private ABStringCallback key;
	private ABBooleanCallback instanceValue;

	@Override
	public Integer callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		if ((this.instanceValue == null) || this.instanceValue.callback(game, caster, localStore, castId)) {
			return (Integer) localStore.get(ABLocalStoreKeys
					.combineUserInstanceKey(this.key.callback(game, caster, localStore, castId), castId));
		}
		else {
			return (Integer) localStore
					.get(ABLocalStoreKeys.combineUserKey(this.key.callback(game, caster, localStore, castId), castId));
		}
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		if (this.instanceValue == null) {
			return "GetLocalStoreUserCastIntegerAU(" + jassTextGenerator.getTriggerLocalStore() + ", "
					+ this.key.generateJassEquivalent(jassTextGenerator) + ", " + jassTextGenerator.getCastId() + ")";
		}
		return "GetStoredIntegerAU(" + jassTextGenerator.getTriggerLocalStore() + ", "
				+ this.key.generateJassEquivalent(jassTextGenerator) + ", " + jassTextGenerator.getCastId() + ", "
				+ this.instanceValue.generateJassEquivalent(jassTextGenerator) + ")";
	}

}
