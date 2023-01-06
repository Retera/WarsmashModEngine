package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;

public class ABCallbackGetStoredFloatByKey extends ABFloatCallback {

	private ABStringCallback key;
	
	@Override
	public Float callback(CSimulation game, CUnit caster, Map<String, Object> localStore) {
		return (Float) localStore.get(key.callback(game, caster, localStore));
	}

}
