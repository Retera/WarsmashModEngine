package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABCallbackIterator extends ABIntegerCallback {

	private ABCallback unique;
	
	@Override
	public Integer callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		if (this.unique != null) {
			return (Integer) localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ITERATORCOUNT+"$"+this.unique.callback(game, caster, localStore, castId), castId));
		}
		else {
			return (Integer) localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ITERATORCOUNT, castId));
		}
	}

}
