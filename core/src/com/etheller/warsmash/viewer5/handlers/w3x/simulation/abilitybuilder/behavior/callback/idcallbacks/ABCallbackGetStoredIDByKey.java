package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks;

import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABCallbackGetStoredIDByKey extends ABIDCallback {

	private ABStringCallback key;
	private ABBooleanCallback instanceValue;
	
	@Override
	public War3ID callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		if (instanceValue == null || instanceValue.callback(game, caster, localStore, castId)) {
			return (War3ID) localStore.get(ABLocalStoreKeys.combineUserInstanceKey(key.callback(game, caster, localStore, castId), castId));
		} else {
			return (War3ID) localStore.get(ABLocalStoreKeys.combineUserKey(key.callback(game, caster, localStore, castId), castId));
		}
	}

}
