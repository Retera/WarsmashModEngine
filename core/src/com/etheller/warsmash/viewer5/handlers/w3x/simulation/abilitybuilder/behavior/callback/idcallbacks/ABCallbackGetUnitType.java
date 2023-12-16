package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks;

import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;

public class ABCallbackGetUnitType extends ABIDCallback {
	
	private ABUnitCallback unit;
	
	@Override
	public War3ID callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		if (unit == null) {
			return caster.getTypeId();
		}
		return unit.callback(game, caster, localStore, castId).getTypeId();
	}

}
