package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.timer;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.timer.ABTimer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;

public class ABActionCreateTimer implements ABAction {

	private ABFloatCallback timeout;
	private ABBooleanCallback repeats;
	private List<ABAction> actions;
	private ABBooleanCallback startTimer;
	private ABFloatCallback delay;
	
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {

		CTimer timer = new ABTimer(caster, localStore, actions, castId);
		timer.setTimeoutTime(timeout.callback(game, caster, localStore, castId));
		localStore.put(ABLocalStoreKeys.LASTCREATEDTIMER, timer);
		
		if (repeats != null && repeats.callback(game, caster, localStore, castId)) {
			timer.setRepeats(true);
			if (startTimer == null || startTimer.callback(game, caster, localStore, castId)) {
				if (delay != null) {
					timer.startRepeatingTimerWithDelay(game, delay.callback(game, caster, localStore, castId));
				} else {
					timer.start(game);
				}
				localStore.put(ABLocalStoreKeys.LASTSTARTEDTIMER, timer);
			}
		} else {
			if (startTimer == null || startTimer.callback(game, caster, localStore, castId)) {
				timer.start(game);
				localStore.put(ABLocalStoreKeys.LASTSTARTEDTIMER, timer);
			}
		}
	}
}
