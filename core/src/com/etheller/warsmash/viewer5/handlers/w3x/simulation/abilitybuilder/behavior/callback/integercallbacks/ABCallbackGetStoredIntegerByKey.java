package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;

public class ABCallbackGetStoredIntegerByKey extends ABIntegerCallback {

	private ABStringCallback key;
	
	@Override
	public Integer callback(CSimulation game, CUnit caster, Map<String, Object> localStore) {
		return (Integer) localStore.get(key.callback(game, caster, localStore));
	}

}
