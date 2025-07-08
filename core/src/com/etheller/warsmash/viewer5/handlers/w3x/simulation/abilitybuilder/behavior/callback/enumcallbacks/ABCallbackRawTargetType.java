package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class ABCallbackRawTargetType extends ABTargetTypeCallback {

	private CTargetType value;
	
	@Override
	public CTargetType callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return value;
	}

}
