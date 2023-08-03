package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.timer;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.timercallbacks.ABTimerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABActionRemoveTimer implements ABAction {

	private ABTimerCallback timer;
	
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore) {
		game.unregisterTimer(timer.callback(game, caster, localStore));
	}
}
