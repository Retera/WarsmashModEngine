package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.list.ABSortableListCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetIntegerFromList extends ABIntegerCallback {

	private ABSortableListCallback<Integer> list;
	private ABIntegerCallback index;

	@Override
	public Integer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return list.callback(caster, localStore, castId).get(index.callback(caster, localStore, castId));
	}

}
