package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class ABCallbackMultiplyFloat extends ABFloatCallback {

	private ABFloatCallback value1;
	private ABFloatCallback value2;
	
	@Override
	public Float callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return value1.callback(game, caster, localStore, castId) * value2.callback(game, caster, localStore, castId);
	}

}
