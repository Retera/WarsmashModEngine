package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.id.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetUnitTypeSpeed extends ABIntegerCallback {

	private ABIDCallback id;

	@Override
	public Integer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return localStore.game.getUnitData().getUnitType(id.callback(caster, localStore, castId)).getSpeed();
	}

}
