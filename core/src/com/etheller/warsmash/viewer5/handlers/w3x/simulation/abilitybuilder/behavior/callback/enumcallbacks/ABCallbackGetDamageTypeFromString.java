package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;

public class ABCallbackGetDamageTypeFromString extends ABDamageTypeCallback {

	private ABStringCallback id;
	
	@Override
	public CDamageType callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return CDamageType.valueOf(id.callback(game, caster, localStore, castId));
	}

}
