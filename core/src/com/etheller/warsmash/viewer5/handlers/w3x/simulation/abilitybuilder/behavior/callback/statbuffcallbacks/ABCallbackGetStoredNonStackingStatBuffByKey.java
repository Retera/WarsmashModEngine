package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.statbuffcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;

public class ABCallbackGetStoredNonStackingStatBuffByKey extends ABNonStackingStatBuffCallback {
	private ABStringCallback key;

	@Override
	public NonStackingStatBuff callback(CSimulation game, CUnit caster, Map<String, Object> localStore) {
		return (NonStackingStatBuff) localStore.get(key.callback(game, caster, localStore));
	}

}
