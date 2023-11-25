package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast.AutocastType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;

public class ABCallbackGetAutocastTypeFromString extends ABAutocastTypeCallback {

	private ABStringCallback id;
	
	@Override
	public AutocastType callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return AutocastType.valueOf(id.callback(game, caster, localStore, castId));
	}

}
