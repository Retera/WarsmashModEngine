package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;

public class ABCallbackCreateLocationFromOffset extends ABLocationCallback {

	private ABLocationCallback origin;
	private ABFloatCallback dist;
	private ABFloatCallback angle;
	
	@Override
	public AbilityPointTarget callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		AbilityPointTarget orig = origin.callback(game, caster, localStore, castId);
		float d = dist.callback(game, caster, localStore, castId);
		float a = angle.callback(game, caster, localStore, castId);
		
		return new AbilityPointTarget((float)(orig.x + d * Math.cos(a)), (float)(orig.y + d * Math.sin(a)));
	}

}
