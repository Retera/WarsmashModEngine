package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitqueue;

import java.util.Map;
import java.util.Queue;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class ABCallbackGetUnitQueueByName extends ABUnitQueueCallback {

	private String name;

	@SuppressWarnings("unchecked")
	@Override
	public Queue<CUnit> callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return (Queue<CUnit>) localStore.get("_unitqueue_" + name);
	}
}
