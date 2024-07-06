package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.parsers.jass.JassTextGeneratorType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABCallbackGetProjectileHitUnit extends ABUnitCallback {

	@Override
	public CUnit callback(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		return (CUnit) localStore.get(ABLocalStoreKeys.PROJECTILEHITUNIT + castId);
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return jassTextGenerator.getUserDataExpr(
				"AB_LOCAL_STORE_KEY_PROJECTILEHITUNIT + I2S(" + jassTextGenerator.getCastId() + ")",
				JassTextGeneratorType.UnitHandle);
	}

}
