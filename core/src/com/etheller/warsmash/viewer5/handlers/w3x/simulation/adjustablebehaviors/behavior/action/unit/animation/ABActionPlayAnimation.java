package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unit.animation;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABActionPlayAnimation implements ABSingleAction {

	private ABUnitCallback unit;
	private ABStringCallback tag;

	private ABBooleanCallback force;
	private ABFloatCallback speed;
	private ABBooleanCallback allowVariations;

	@Override
	public void runAction(CUnit caster, ABLocalDataStore localStore, final int castId) {
		final CUnit targetUnit = this.unit.callback(caster, localStore, castId);
		boolean f = true;
		float s = 1f;
		boolean av = false;
		if (this.force != null) {
			f = this.force.callback(caster, localStore, castId);
		}
		if (this.speed != null) {
			s = this.speed.callback(caster, localStore, castId);
		}
		if (this.allowVariations != null) {
			av = this.allowVariations.callback(caster, localStore, castId);
		}

		targetUnit.getUnitAnimationListener().playAnimation(f,
				PrimaryTag.valueOf(this.tag.callback(caster, localStore, castId)), SequenceUtils.EMPTY, s, av);
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		// TODO: the stuff exposed by warsmash below is not in jass yet
		if ((this.force != null) && !this.force.generateJassEquivalent(jassTextGenerator).equals("true")) {
			throw new UnsupportedOperationException();
		}
		if ((this.allowVariations != null)
				&& !this.allowVariations.generateJassEquivalent(jassTextGenerator).equals("true")) {
			throw new UnsupportedOperationException();
		}
		if (this.speed != null) {
			throw new UnsupportedOperationException();
		}
		return "SetUnitAnimation(" + this.unit.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.tag.generateJassEquivalent(jassTextGenerator) + ")";
	}

}
