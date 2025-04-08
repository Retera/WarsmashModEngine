package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;

public class ABCallbackGetDistanceBetweenUnits extends ABFloatCallback {

	private ABUnitCallback origin;
	private ABUnitCallback target;
	
	@Override
	public Float callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		CUnit o = origin.callback(game, caster, localStore, castId);
		CUnit t = target.callback(game, caster, localStore, castId);
		
		return (float) o.distance(t);
	}

}
