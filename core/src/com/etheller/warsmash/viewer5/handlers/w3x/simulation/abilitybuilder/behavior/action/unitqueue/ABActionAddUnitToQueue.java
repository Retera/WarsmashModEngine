package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unitqueue;

import java.util.Map;
import java.util.Queue;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitqueue.ABUnitQueueCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionAddUnitToQueue implements ABAction {

	private ABUnitQueueCallback queue;
	private ABUnitCallback unit;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		Queue<CUnit> groupQueue = queue.callback(game, caster, localStore, castId);
		CUnit rUnit = unit.callback(game, caster, localStore, castId);
		groupQueue.add(rUnit);
		localStore.put(ABLocalStoreKeys.LASTADDEDUNIT, rUnit);
	}
}
