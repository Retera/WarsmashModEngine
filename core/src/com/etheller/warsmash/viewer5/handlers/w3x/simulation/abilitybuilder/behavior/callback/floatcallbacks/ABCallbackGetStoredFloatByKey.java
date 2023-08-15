package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABCallbackGetStoredFloatByKey extends ABFloatCallback {

	private ABStringCallback key;
	private ABBooleanCallback instanceValue;
	
	@Override
	public Float callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		if (instanceValue == null || instanceValue.callback(game, caster, localStore, castId)) {
			return (Float) localStore.get(ABLocalStoreKeys.combineUserInstanceKey(key.callback(game, caster, localStore, castId), castId));
		} else {
			return (Float) localStore.get(ABLocalStoreKeys.combineUserKey(key.callback(game, caster, localStore, castId), castId));
		}
	}

}
