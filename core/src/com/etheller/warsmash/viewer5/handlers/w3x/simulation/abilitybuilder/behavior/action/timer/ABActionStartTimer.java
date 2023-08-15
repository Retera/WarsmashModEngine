package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.timer;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.timercallbacks.ABTimerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionStartTimer implements ABAction {

	private ABTimerCallback timer;
	
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		timer.callback(game, caster, localStore, castId).start(game);
		localStore.put(ABLocalStoreKeys.LASTSTARTEDTIMER, timer);
	}
}
