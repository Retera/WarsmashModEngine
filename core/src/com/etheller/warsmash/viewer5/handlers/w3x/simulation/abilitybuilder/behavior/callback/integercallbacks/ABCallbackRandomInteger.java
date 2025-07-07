package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class ABCallbackRandomInteger extends ABIntegerCallback {

	private ABIntegerCallback lowerBound;
	private ABIntegerCallback upperBound;
	
	private ABIntegerCallback bound;
	
	@Override
	public Integer callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		if (bound != null) {
			return game.getSeededRandom().nextInt(bound.callback(game, caster, localStore, castId));
		}
		int low = Integer.MIN_VALUE;
		int high = Integer.MAX_VALUE;
		if (upperBound != null) {
			low = 0;
			high = upperBound.callback(game, caster, localStore, castId);
		}
		if (lowerBound != null) {
			low = lowerBound.callback(game, caster, localStore, castId);
		}
		return game.getSeededRandom().nextInt(low,high);
	}

}
