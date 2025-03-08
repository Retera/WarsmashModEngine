package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.gamestate;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;

public class ABActionSetFalseTimeOfDay implements ABSingleAction {

	private ABIntegerCallback hour;
	private ABIntegerCallback minute;
	private ABFloatCallback duration;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		game.addFalseTimeOfDay(this.hour.callback(game, caster, localStore, castId),
				this.minute.callback(game, caster, localStore, castId),
				this.duration.callback(game, caster, localStore, castId));
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "SetFalseTimeOfDay(" + this.hour.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.minute.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.duration.generateJassEquivalent(jassTextGenerator) + ")";
	}
}
