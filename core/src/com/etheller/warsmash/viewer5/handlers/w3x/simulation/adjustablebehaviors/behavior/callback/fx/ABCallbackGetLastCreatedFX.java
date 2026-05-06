package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.fx;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class ABCallbackGetLastCreatedFX extends ABFXCallback {

	@Override
	public SimulationRenderComponent callback(CUnit caster, ABLocalDataStore localStore,
			final int castId) {
		return localStore.get(ABLocalStoreKeys.LASTCREATEDFX, SimulationRenderComponent.class);
	}

}
