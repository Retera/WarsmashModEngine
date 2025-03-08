package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.eventcallbacks.timeeventcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.event.ABTimeOfDayEvent;

public class ABCallbackInlineConditionTimeOfDayEvent extends ABTimeOfDayEventCallback {

	private ABCondition condition;
	private ABTimeOfDayEventCallback pass;
	private ABTimeOfDayEventCallback fail;
	
	@Override
	public ABTimeOfDayEvent callback(CSimulation game, CUnit caster, Map<String, Object> localStore, int castId) {
		if (condition != null && condition.callback(game, caster, localStore, castId)) {
			return pass.callback(game, caster, localStore, castId);
		}
		return fail.callback(game, caster, localStore, castId);
	}

}
