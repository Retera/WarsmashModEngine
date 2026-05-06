package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unitgroup;

import java.util.Set;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetUnitGroupByName extends ABUnitGroupCallback {

	private String name;

	@SuppressWarnings("unchecked")
	@Override
	public Set<CUnit> callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return (Set<CUnit>) localStore.get("_unitgroup_" + name);
	}
}
