package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.longcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class ABCallbackMultiplyLong extends ABLongCallback {

	private ABLongCallback value1;
	private ABLongCallback value2;
	
	@Override
	public Long callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return value1.callback(game, caster, localStore, castId) * value2.callback(game, caster, localStore, castId);
	}

}
