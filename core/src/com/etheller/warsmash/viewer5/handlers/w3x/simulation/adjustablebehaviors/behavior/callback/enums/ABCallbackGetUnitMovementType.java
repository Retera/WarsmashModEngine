package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums;

import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetUnitMovementType extends ABMovementTypeCallback {

	private ABUnitCallback unit;

	@Override
	public MovementType callback(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		return unit.callback(caster, localStore, castId).getMovementType();
	}

}
