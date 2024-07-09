package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.parsers.jass.JassTextGeneratorType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABCallbackGetTargetedLocation extends ABLocationCallback {

	@Override
	public AbilityPointTarget callback(CSimulation game, CUnit caster, Map<String, Object> localStore,
			final int castId) {
		final AbilityPointTarget target = (AbilityPointTarget) localStore
				.get(ABLocalStoreKeys.ABILITYTARGETEDLOCATION + castId);

		return target;
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return jassTextGenerator.getUserDataExpr(
				"AB_LOCAL_STORE_KEY_ABILITYTARGETEDLOCATION + I2S(" + jassTextGenerator.getCastId() + ")",
				JassTextGeneratorType.LocationHandle);
	}

}
