package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.structural;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCallback;

public class ABActionStoreValueLocally implements ABAction {

	private ABStringCallback key;
	private ABCallback valueToStore;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore) {
		localStore.put(key.callback(game, caster, localStore), valueToStore.callback(game, caster, localStore));
	}
}
