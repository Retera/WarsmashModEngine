package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class ABCallbackSin extends ABFloatCallback {
	
	private ABFloatCallback angle;

	@Override
	public Float callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return (float) StrictMath.sin(angle.callback(game, caster, localStore, castId));
	}

}
