package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.listenercallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.listener.ABDeathReplacementEffect;

public class ABCallbackGetStoredDeathReplacementByKey extends ABDeathReplacementCallback {
	private ABStringCallback key;

	@Override
	public ABDeathReplacementEffect callback(CSimulation game, CUnit caster, Map<String, Object> localStore) {
		return (ABDeathReplacementEffect) localStore.get(key.callback(game, caster, localStore));
	}

}
