package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.listenercallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.listener.ABAttackPostDamageListener;

public class ABCallbackGetStoredAttackPostDamageListenerByKey extends ABAttackPostDamageListenerCallback {
	private ABStringCallback key;
	private ABBooleanCallback instanceValue;

	@Override
	public ABAttackPostDamageListener callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		if (instanceValue == null || instanceValue.callback(game, caster, localStore, castId)) {
			return (ABAttackPostDamageListener) localStore.get(ABLocalStoreKeys.combineUserInstanceKey(key.callback(game, caster, localStore, castId), castId));
		} else {
			return (ABAttackPostDamageListener) localStore.get(ABLocalStoreKeys.combineUserKey(key.callback(game, caster, localStore, castId), castId));
		}
	}

}
