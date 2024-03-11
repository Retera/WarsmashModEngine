package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.gamestate;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABActionSetFalseTimeOfDay implements ABAction {

	private ABIntegerCallback hour;
	private ABIntegerCallback minute;
	private ABFloatCallback duration;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		game.addFalseTimeOfDay(hour.callback(game, caster, localStore, castId), minute.callback(game, caster, localStore, castId), duration.callback(game, caster, localStore, castId));
	}
}
