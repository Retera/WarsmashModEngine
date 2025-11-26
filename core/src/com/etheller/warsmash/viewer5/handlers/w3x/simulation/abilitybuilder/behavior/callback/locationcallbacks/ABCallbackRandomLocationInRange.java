package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;

public class ABCallbackRandomLocationInRange extends ABLocationCallback {

	private ABLocationCallback origin;
	private ABFloatCallback range;

	@Override
	public AbilityPointTarget callback(CSimulation game, CUnit caster, Map<String, Object> localStore,
			final int castId) {
		final AbilityPointTarget orig = this.origin.callback(game, caster, localStore, castId);
		final float d = this.range.callback(game, caster, localStore, castId) * game.getSeededRandom().nextFloat();
		final float a = (float) (Math.PI * 2 * game.getSeededRandom().nextFloat());
		
		orig.add((float)(d * Math.cos(a)), (float)(d * Math.sin(a)));
		return orig;
	}

}
