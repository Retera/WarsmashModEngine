package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.longcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;

public class ABCallbackCreateDetectorData extends ABLongCallback {

	private ABIntegerCallback detectionLevel;
	private ABFloatCallback range;
	
	@Override
	public Long callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		long rng = Math.min(PathingGrid.getFogOfWarDistance(range.callback(game, caster, localStore, castId)), 8388607);
		byte val = detectionLevel.callback(game, caster, localStore, castId).byteValue();
		return val + (rng<<8);
	}

}
