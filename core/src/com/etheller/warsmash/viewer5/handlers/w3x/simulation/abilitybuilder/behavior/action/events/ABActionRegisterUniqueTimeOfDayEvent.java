package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.events;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.eventcallbacks.timeeventcallbacks.ABTimeOfDayEventCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.event.ABTimeOfDayEvent;

public class ABActionRegisterUniqueTimeOfDayEvent implements ABAction {

	private ABTimeOfDayEventCallback event;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		ABTimeOfDayEvent ev = event.callback(game, caster, localStore, castId);
		if (!game.isTimeOfDayEventRegistered(ev)) {
			System.err.println("REGISTERED TIME OF DAY EVENT: " + ev);
			game.registerTimeOfDayEvent(ev);
		}
		
	}
}
