package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.location;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackCreateLocationFromOffset extends ABLocationCallback {

	private ABLocationCallback origin;
	private ABFloatCallback dist;
	private ABFloatCallback angle;
	
	private ABBooleanCallback angleInDegrees;

	@Override
	public AbilityPointTarget callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		final AbilityPointTarget orig = this.origin.callback(caster, localStore, castId);
		final float d = this.dist.callback(caster, localStore, castId);
		final float a = this.angle.callback(caster, localStore, castId);
		
		if (angleInDegrees != null && angleInDegrees.callback(caster, localStore, castId)) {
			return new AbilityPointTarget((float) (orig.x + (d * Math.cos(Math.toRadians(a)))), (float) (orig.y + (d * Math.sin(Math.toRadians(a)))));
		}
		return new AbilityPointTarget((float) (orig.x + (d * Math.cos(a))), (float) (orig.y + (d * Math.sin(a))));
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "PolarProjectionAU(" + this.origin.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.dist.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.angle.generateJassEquivalent(jassTextGenerator) + ")";
	}

}
