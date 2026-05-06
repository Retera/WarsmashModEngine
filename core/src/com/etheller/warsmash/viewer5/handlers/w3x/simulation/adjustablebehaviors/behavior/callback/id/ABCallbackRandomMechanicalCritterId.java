package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.handler.ABRandomTypeHandler;

public class ABCallbackRandomMechanicalCritterId extends ABIDCallback {

	@Override
	public War3ID callback(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		CUnitType id = ABRandomTypeHandler.getRandomMechanicalCritterType(localStore.game);
		if (id == null) {
			return null;
		}
		return id.getTypeId();
	}

}
