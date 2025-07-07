package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.timer;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.timercallbacks.ABTimerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;

public class ABActionUpdateTimerTimeout implements ABSingleAction {

	private ABTimerCallback timer;
	private ABFloatCallback timeout;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		CTimer theT = this.timer.callback(game, caster, localStore, castId);
		theT.resetTimeoutTime(this.timeout.callback(game, caster, localStore, castId));
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "ABTimerSetTimeoutTime(" + this.timer.generateJassEquivalent(jassTextGenerator) + ", "
				+ this.timeout.generateJassEquivalent(jassTextGenerator) + ")";
	}
}
