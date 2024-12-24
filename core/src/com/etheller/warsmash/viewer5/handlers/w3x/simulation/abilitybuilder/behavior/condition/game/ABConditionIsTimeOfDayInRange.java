package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.game;

import java.util.Map;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;

public class ABConditionIsTimeOfDayInRange implements ABCondition {

	private ABFloatCallback startTime;
	private ABFloatCallback endTime;
	
	@Override
	public boolean evaluate(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		float st = 0;
		float et = Float.MAX_VALUE;
		if (startTime != null) {
			st = startTime.callback(game, caster, localStore, castId);
		}
		if (endTime != null) {
			et = endTime.callback(game, caster, localStore, castId);
		}
		return st <= et ? game.getGameTimeOfDay() >= st && game.getGameTimeOfDay() < et
				: game.getGameTimeOfDay() >= st || game.getGameTimeOfDay() < et;
	}

}
