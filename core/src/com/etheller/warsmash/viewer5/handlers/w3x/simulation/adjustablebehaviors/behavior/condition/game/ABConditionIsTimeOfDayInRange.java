package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.game;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABConditionIsTimeOfDayInRange extends ABBooleanCallback {

	private ABFloatCallback startTime;
	private ABFloatCallback endTime;

	@Override
	public Boolean callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		float st = 0;
		float et = Float.MAX_VALUE;
		if (startTime != null) {
			st = startTime.callback(caster, localStore, castId);
		}
		if (endTime != null) {
			et = endTime.callback(caster, localStore, castId);
		}
		return st <= et ? localStore.game.getGameTimeOfDay() >= st && localStore.game.getGameTimeOfDay() < et
				: localStore.game.getGameTimeOfDay() >= st || localStore.game.getGameTimeOfDay() < et;
	}

}
