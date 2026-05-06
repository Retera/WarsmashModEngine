package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unit.animation;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABActionRemoveSecondaryAnimationTag implements ABSingleAction {

	private ABUnitCallback unit;
	private ABStringCallback tag;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		final CUnit targetUnit = this.unit.callback(caster, localStore, castId);
		if (targetUnit.getUnitAnimationListener()
				.removeSecondaryTag(SecondaryTag.valueOf(this.tag.callback(caster, localStore, castId)))) {
			targetUnit.getUnitAnimationListener().forceResetCurrentAnimation();
		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "AddUnitAnimationProperties(" + this.unit.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.tag.generateJassEquivalent(jassTextGenerator) + ", false)";
	}

}
