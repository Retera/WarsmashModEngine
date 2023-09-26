package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;

public class ABCallbackGetUnitAcquisitionRange extends ABFloatCallback {

	private ABUnitCallback unit;
	
	@Override
	public Float callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		CUnit theUnit = unit.callback(game, caster, localStore, castId);
		if (theUnit.getAcquisitionRange() > 0) {
			return theUnit.getAcquisitionRange();
		}
		return (float) theUnit.getUnitType().getSightRadiusDay();
	}

}
