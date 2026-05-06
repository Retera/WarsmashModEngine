package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.list.integer;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackEmptyIntegerList extends ABIntegerListCallback {

	@Override
	public List<Integer> callback(CUnit caster, ABLocalDataStore localStore, int castId) {
		return new ArrayList<>();
	}

}
