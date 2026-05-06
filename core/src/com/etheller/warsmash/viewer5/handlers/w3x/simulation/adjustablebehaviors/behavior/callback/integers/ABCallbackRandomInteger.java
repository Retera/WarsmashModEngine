package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackRandomInteger extends ABIntegerCallback {

	private ABIntegerCallback lowerBound;
	private ABIntegerCallback upperBound;

	private ABIntegerCallback bound;

	@Override
	public Integer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		if (bound != null) {
			return localStore.game.getSeededRandom().nextInt(bound.callback(caster, localStore, castId));
		}
		int low = Integer.MIN_VALUE;
		int high = Integer.MAX_VALUE;
		if (upperBound != null) {
			low = 0;
			high = upperBound.callback(caster, localStore, castId);
		}
		if (lowerBound != null) {
			low = lowerBound.callback(caster, localStore, castId);
		}
		return localStore.game.getSeededRandom().nextInt(low, high);
	}

}
