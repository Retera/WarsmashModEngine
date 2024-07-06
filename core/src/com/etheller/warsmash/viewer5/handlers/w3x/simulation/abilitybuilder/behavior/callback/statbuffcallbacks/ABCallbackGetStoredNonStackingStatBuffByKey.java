package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.statbuffcallbacks;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;

public class ABCallbackGetStoredNonStackingStatBuffByKey extends ABNonStackingStatBuffCallback {
	private ABStringCallback key;
	private ABBooleanCallback instanceValue;

	@Override
	public NonStackingStatBuff callback(final CSimulation game, final CUnit caster,
			final Map<String, Object> localStore, final int castId) {
		if ((this.instanceValue == null) || this.instanceValue.callback(game, caster, localStore, castId)) {
			return (NonStackingStatBuff) localStore.get(ABLocalStoreKeys
					.combineUserInstanceKey(this.key.callback(game, caster, localStore, castId), castId));
		}
		else {
			return (NonStackingStatBuff) localStore
					.get(ABLocalStoreKeys.combineUserKey(this.key.callback(game, caster, localStore, castId), castId));
		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		if (this.instanceValue == null) {
			return "GetLocalStoreUserCastNonStackingStatBuffHandleAU(" + jassTextGenerator.getTriggerLocalStore() + ", "
					+ this.key.generateJassEquivalent(jassTextGenerator) + ", " + jassTextGenerator.getCastId() + ")";
		}
		return "GetStoredNonStackingStatBuffAU(" + jassTextGenerator.getTriggerLocalStore() + ", "
				+ this.key.generateJassEquivalent(jassTextGenerator) + ", " + jassTextGenerator.getCastId() + ", "
				+ this.instanceValue.generateJassEquivalent(jassTextGenerator) + ")";
	}

}
