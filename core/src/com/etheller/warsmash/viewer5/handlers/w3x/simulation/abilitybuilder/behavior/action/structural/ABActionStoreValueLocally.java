package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.structural;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionStoreValueLocally implements ABAction {

	private ABStringCallback key;
	private ABBooleanCallback instanceValue;
	private ABCallback valueToStore;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		if (instanceValue == null || instanceValue.callback(game, caster, localStore, castId)) {
			localStore.put(ABLocalStoreKeys.combineUserInstanceKey(key.callback(game, caster, localStore, castId), castId), valueToStore.callback(game, caster, localStore, castId));
		} else {
			localStore.put(ABLocalStoreKeys.combineUserKey(key.callback(game, caster, localStore, castId), castId), valueToStore.callback(game, caster, localStore, castId));
		}
	}
}
