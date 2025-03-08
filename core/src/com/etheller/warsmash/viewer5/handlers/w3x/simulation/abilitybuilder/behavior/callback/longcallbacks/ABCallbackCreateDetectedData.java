package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.longcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;

public class ABCallbackCreateDetectedData extends ABLongCallback {

	private ABIntegerCallback detectionLevel;
	private ABIntegerCallback player;
	
	@Override
	public Long callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		long ply = player.callback(game, caster, localStore, castId);
		byte val = detectionLevel.callback(game, caster, localStore, castId).byteValue();
		return val + (ply<<8);
	}

}
