package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.listenercallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.listener.ABDeathReplacementEffect;

public class ABCallbackGetLastCreatedDeathReplacement extends ABDeathReplacementCallback {

	@Override
	public ABDeathReplacementEffect callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return (ABDeathReplacementEffect) localStore.get(ABLocalStoreKeys.LASTCREATEDDRE);
	}

}
