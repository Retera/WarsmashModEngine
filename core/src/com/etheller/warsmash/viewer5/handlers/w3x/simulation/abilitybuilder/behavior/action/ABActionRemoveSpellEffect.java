package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.fxcallbacks.ABFXCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;

public class ABActionRemoveSpellEffect implements ABSingleAction {

	private ABFXCallback effectToRemove;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		this.effectToRemove.callback(game, caster, localStore, castId).remove();
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "DestroyEffect(" + this.effectToRemove.generateJassEquivalent(jassTextGenerator) + ")";
	}
}
