package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.timercallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;

public class ABCallbackGetStoredTimerByKey extends ABTimerCallback {
	private ABStringCallback key;

	@Override
	public CTimer callback(CSimulation game, CUnit caster, Map<String, Object> localStore) {
		return (CTimer) localStore.get(key.callback(game, caster, localStore));
	}

}
