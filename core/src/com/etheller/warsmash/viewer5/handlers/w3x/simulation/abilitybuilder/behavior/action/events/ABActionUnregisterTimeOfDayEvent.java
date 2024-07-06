package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.events;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.eventcallbacks.timeeventcallbacks.ABTimeOfDayEventCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;

public class ABActionUnregisterTimeOfDayEvent implements ABSingleAction {

	private ABTimeOfDayEventCallback event;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		game.unregisterTimeOfDayEvent(this.event.callback(game, caster, localStore, castId));
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "UnregisterABTimeOfDayEvent(" + this.event.generateJassEquivalent(jassTextGenerator) + ")";
	}
}
