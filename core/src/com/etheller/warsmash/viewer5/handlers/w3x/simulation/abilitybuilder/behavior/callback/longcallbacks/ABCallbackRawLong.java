package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.longcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class ABCallbackRawLong extends ABLongCallback {
	
	private Long value;
	
	@Override
	public Long callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return value;
	}

}
