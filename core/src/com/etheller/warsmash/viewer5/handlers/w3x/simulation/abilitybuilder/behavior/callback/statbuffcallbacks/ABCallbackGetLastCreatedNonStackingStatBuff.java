package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.statbuffcallbacks;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.parsers.jass.JassTextGeneratorType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;

public class ABCallbackGetLastCreatedNonStackingStatBuff extends ABNonStackingStatBuffCallback {

	@Override
	public NonStackingStatBuff callback(final CSimulation game, final CUnit caster,
			final Map<String, Object> localStore, final int castId) {
		return (NonStackingStatBuff) localStore.get(ABLocalStoreKeys.LASTCREATEDNSSB);
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return jassTextGenerator.getUserDataExpr("AB_LOCAL_STORE_KEY_LASTCREATEDNSSB",
				JassTextGeneratorType.NonStackingStatBuffHandle);
	}

}
