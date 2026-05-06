package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackOneGameTick extends ABFloatCallback {

	@Override
	public Float callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return WarsmashConstants.SIMULATION_STEP_TIME + 0.000001f;
	}

}
