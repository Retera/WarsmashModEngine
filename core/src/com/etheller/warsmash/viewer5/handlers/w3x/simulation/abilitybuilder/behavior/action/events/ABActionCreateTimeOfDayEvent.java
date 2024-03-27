package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.events;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.event.ABTimeOfDayEvent;

public class ABActionCreateTimeOfDayEvent implements ABAction {

	private List<ABAction> actions;
	private ABFloatCallback startTime;
	private ABFloatCallback endTime;
	
	private ABStringCallback equalityId;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		String eqId = null;
		float st = 0;
		float et = Float.MAX_VALUE;
		if (equalityId != null) {
			eqId = equalityId.callback(game, caster, localStore, castId);
		}
		if (startTime != null) {
			st = startTime.callback(game, caster, localStore, castId);
		}
		if (endTime != null) {
			et = endTime.callback(game, caster, localStore, castId);
		}
		
		ABTimeOfDayEvent event = new ABTimeOfDayEvent(game, caster, localStore, castId, actions, st, et, eqId);

		localStore.put(ABLocalStoreKeys.LASTCREATEDTODEVENT, event);
	}
}
