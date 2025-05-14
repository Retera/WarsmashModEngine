package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;

public class ABCallbackModifyLocationWithOffset extends ABLocationCallback {

	private ABLocationCallback origin;
	private ABFloatCallback dist;
	private ABFloatCallback angle;
	
	private ABBooleanCallback angleInDegrees;

	@Override
	public AbilityPointTarget callback(CSimulation game, CUnit caster, Map<String, Object> localStore,
			final int castId) {
		final AbilityPointTarget orig = this.origin.callback(game, caster, localStore, castId);
		final float d = this.dist.callback(game, caster, localStore, castId);
		final float a = this.angle.callback(game, caster, localStore, castId);
		
		if (angleInDegrees != null && angleInDegrees.callback(game, caster, localStore, castId)) {
			orig.add((float)(d * Math.cos(Math.toRadians(a))), (float)(d * Math.sin(Math.toRadians(a))));
			return orig;
		}
		orig.add((float)(d * Math.cos(a)), (float)(d * Math.sin(a)));
		return orig;
	}

}
