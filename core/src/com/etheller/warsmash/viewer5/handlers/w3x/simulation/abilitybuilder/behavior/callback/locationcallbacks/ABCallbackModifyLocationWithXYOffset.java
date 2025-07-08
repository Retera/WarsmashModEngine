package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;

public class ABCallbackModifyLocationWithXYOffset extends ABLocationCallback {

	private ABLocationCallback origin;
	private ABFloatCallback xdist;
	private ABFloatCallback ydist;
	
	@Override
	public AbilityPointTarget callback(CSimulation game, CUnit caster, Map<String, Object> localStore,
			final int castId) {
		final AbilityPointTarget orig = this.origin.callback(game, caster, localStore, castId);
		orig.add(xdist.callback(game, caster, localStore, castId), ydist.callback(game, caster, localStore, castId));
		return orig;
	}

}
