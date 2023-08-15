package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unitlisteners.internalCallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABCallbackGetTotalDamageDealt extends ABFloatCallback {
	
	@Override
	public Float callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return (Float) localStore.get(ABLocalStoreKeys.TOTALDAMAGEDEALT+castId);
	}

}
