package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums;

import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetMovementTypeFromString extends ABMovementTypeCallback {

	private ABStringCallback id;

	@Override
	public MovementType callback(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		return MovementType.valueOf(this.id.callback(caster, localStore, castId));
	}

}
