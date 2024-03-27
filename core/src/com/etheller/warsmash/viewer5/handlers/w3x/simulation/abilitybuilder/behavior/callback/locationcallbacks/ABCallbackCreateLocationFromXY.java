package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;

public class ABCallbackCreateLocationFromXY extends ABLocationCallback {

	private ABFloatCallback x;
	private ABFloatCallback y;
	
	@Override
	public AbilityPointTarget callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return new AbilityPointTarget(x.callback(game, caster, localStore, castId), y.callback(game, caster, localStore, castId));
	}

}
