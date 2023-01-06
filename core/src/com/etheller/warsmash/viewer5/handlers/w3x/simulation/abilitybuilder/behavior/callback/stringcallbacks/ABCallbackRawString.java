package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class ABCallbackRawString extends ABStringCallback {
	
	private String value;
	
	@Override
	public String callback(CSimulation game, CUnit caster, Map<String, Object> localStore) {
		return value;
	}

}
