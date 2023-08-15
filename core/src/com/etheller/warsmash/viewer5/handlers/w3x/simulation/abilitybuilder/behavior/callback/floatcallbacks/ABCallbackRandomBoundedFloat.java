package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class ABCallbackRandomBoundedFloat extends ABFloatCallback {

	private ABFloatCallback bound;
	
	@Override
	public Float callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return game.getSeededRandom().nextFloat(bound.callback(game, caster, localStore, castId));
	}

}
