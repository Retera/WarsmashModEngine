package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;

public class ABCallbackGetUnitLocation extends ABLocationCallback {

	private ABUnitCallback unit;
	
	@Override
	public AbilityPointTarget callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		CUnit theUnit = unit.callback(game, caster, localStore, castId);
		
		return new AbilityPointTarget(theUnit.getX(), theUnit.getY());
	}

}
