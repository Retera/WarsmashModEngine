package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.eventcallbacks.timeeventcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.event.ABTimeOfDayEvent;

public class ABCallbackGetLastCreatedTimeOfDayEvent extends ABTimeOfDayEventCallback {

	@Override
	public ABTimeOfDayEvent callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return (ABTimeOfDayEvent) localStore.get(ABLocalStoreKeys.LASTCREATEDTODEVENT);
	}

}
