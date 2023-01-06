package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class ABCallbackRawBoolean extends ABBooleanCallback {
	
	private Boolean value;
	
	@Override
	public Boolean callback(CSimulation game, CUnit caster, Map<String, Object> localStore) {
		return value;
	}

}
