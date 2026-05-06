package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.longs;

import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackCreateDetectorData extends ABLongCallback {

	private ABIntegerCallback detectionLevel;
	private ABFloatCallback range;
	
	@Override
	public Long callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		long rng = Math.min(PathingGrid.getFogOfWarDistance(range.callback(caster, localStore, castId)), 8388607);
		byte val = detectionLevel.callback(caster, localStore, castId).byteValue();
		return val + (rng<<8);
	}

}
