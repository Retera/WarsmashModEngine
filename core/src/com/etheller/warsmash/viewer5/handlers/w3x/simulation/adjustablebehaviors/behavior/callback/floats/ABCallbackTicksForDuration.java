package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackTicksForDuration extends ABFloatCallback {

	private ABFloatCallback duration;

	@Override
	public Float callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return duration.callback(caster, localStore, castId) / WarsmashConstants.SIMULATION_STEP_TIME;
	}

}
