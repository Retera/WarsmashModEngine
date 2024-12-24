package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks.ABLocationCallback;

public class ABCallbackGetAngleBetweenLocations extends ABFloatCallback {

	private ABLocationCallback origin;
	private ABLocationCallback target;

	@Override
	public Float callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		final AbilityPointTarget o = this.origin.callback(game, caster, localStore, castId);
		final AbilityPointTarget t = this.target.callback(game, caster, localStore, castId);

		final double dx = t.getX() - o.getX();
		final double dy = t.getY() - o.getY();
		return (float) StrictMath.atan2(dy, dx);
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "AngleBetweenPointsAU(" + this.origin.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.target.generateJassEquivalent(jassTextGenerator) + ")";
	}

}
