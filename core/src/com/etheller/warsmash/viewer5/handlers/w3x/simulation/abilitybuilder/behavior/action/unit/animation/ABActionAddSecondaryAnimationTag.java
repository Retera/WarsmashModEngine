package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unit.animation;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;

public class ABActionAddSecondaryAnimationTag implements ABSingleAction {

	private ABUnitCallback unit;
	private ABStringCallback tag;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final CUnit targetUnit = this.unit.callback(game, caster, localStore, castId);
		if (targetUnit.getUnitAnimationListener()
				.addSecondaryTag(SecondaryTag.valueOf(this.tag.callback(game, caster, localStore, castId)))) {
			targetUnit.getUnitAnimationListener().forceResetCurrentAnimation();
		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "AddUnitAnimationProperties(" + this.unit.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.tag.generateJassEquivalent(jassTextGenerator) + ", true)";
	}

}
