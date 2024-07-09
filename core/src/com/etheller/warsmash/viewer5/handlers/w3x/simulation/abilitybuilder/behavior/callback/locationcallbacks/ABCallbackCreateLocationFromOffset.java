package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;

public class ABCallbackCreateLocationFromOffset extends ABLocationCallback {

	private ABLocationCallback origin;
	private ABFloatCallback dist;
	private ABFloatCallback angle;

	@Override
	public AbilityPointTarget callback(CSimulation game, CUnit caster, Map<String, Object> localStore,
			final int castId) {
		final AbilityPointTarget orig = this.origin.callback(game, caster, localStore, castId);
		final float d = this.dist.callback(game, caster, localStore, castId);
		final float a = this.angle.callback(game, caster, localStore, castId);

		return new AbilityPointTarget((float) (orig.x + (d * Math.cos(a))), (float) (orig.y + (d * Math.sin(a))));
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "PolarProjectionAU(" + this.origin.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.dist.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.angle.generateJassEquivalent(jassTextGenerator) + ")";
	}

}
