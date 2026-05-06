package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackRandomBoundedFloat extends ABFloatCallback {

	private ABFloatCallback bound;

	@Override
	public Float callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return localStore.game.getSeededRandom().nextFloat(bound.callback(caster, localStore, castId));
	}

}
