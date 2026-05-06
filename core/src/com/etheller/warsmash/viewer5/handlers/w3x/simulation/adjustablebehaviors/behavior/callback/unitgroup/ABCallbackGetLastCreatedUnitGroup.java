package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unitgroup;

import java.util.Set;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABCallbackGetLastCreatedUnitGroup extends ABUnitGroupCallback {

	@SuppressWarnings("unchecked")
	@Override
	public Set<CUnit> callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return (Set<CUnit>) localStore.get(ABLocalStoreKeys.LASTCREATEDUNITGROUP);
	}

}
