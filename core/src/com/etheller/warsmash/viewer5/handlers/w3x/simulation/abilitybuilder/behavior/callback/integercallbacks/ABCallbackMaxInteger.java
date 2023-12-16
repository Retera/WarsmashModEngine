package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class ABCallbackMaxInteger extends ABIntegerCallback {

	private ABIntegerCallback value1;
	private ABIntegerCallback value2;
	
	@Override
	public Integer callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return Math.max(value1.callback(game, caster, localStore, castId), value2.callback(game, caster, localStore, castId));
	}
 
}
