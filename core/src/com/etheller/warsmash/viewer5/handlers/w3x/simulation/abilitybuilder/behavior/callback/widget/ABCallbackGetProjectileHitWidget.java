package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.widget;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABCallbackGetProjectileHitWidget extends ABWidgetCallback {

	@Override
	public CWidget callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		final CWidget unit = (CWidget) localStore.get(ABLocalStoreKeys.PROJECTILEHITUNIT + castId);
		final CWidget dest = (CWidget) localStore.get(ABLocalStoreKeys.PROJECTILEHITDEST + castId);
		if (unit != null) {
			return unit;
		}
		else {
			return dest;
		}
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "GetProjectileHitWidgetAU()";
	}

}
