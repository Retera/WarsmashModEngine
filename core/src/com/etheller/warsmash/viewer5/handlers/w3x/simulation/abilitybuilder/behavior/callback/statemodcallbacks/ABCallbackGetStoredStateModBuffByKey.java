package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.statemodcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuff;

public class ABCallbackGetStoredStateModBuffByKey extends ABStateModBuffCallback {
	private ABStringCallback key;
	private ABBooleanCallback instanceValue;

	@Override
	public StateModBuff callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		if (instanceValue == null || instanceValue.callback(game, caster, localStore, castId)) {
			return (StateModBuff) localStore.get(ABLocalStoreKeys.combineUserInstanceKey(key.callback(game, caster, localStore, castId), castId));
		} else {
			return (StateModBuff) localStore.get(ABLocalStoreKeys.combineUserKey(key.callback(game, caster, localStore, castId), castId));
		}
	}

}
