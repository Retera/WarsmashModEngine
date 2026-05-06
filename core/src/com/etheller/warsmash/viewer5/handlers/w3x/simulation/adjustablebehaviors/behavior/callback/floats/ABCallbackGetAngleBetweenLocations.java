package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.location.ABLocationCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetAngleBetweenLocations extends ABFloatCallback {

	private ABLocationCallback origin;
	private ABLocationCallback target;

	@Override
	public Float callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		final AbilityPointTarget o = this.origin.callback(caster, localStore, castId);
		final AbilityPointTarget t = this.target.callback(caster, localStore, castId);

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
