package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.statemod;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.StateModBuff;

public class ABCallbackGetLastCreatedStateModBuff extends ABStateModBuffCallback {

	@Override
	public StateModBuff callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return localStore.get(ABLocalStoreKeys.LASTCREATEDSMB, StateModBuff.class);
	}

}
