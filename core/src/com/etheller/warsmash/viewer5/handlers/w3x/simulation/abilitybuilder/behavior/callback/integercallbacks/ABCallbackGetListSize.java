package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.list.ABListCallback;

public class ABCallbackGetListSize extends ABIntegerCallback {
	
	private ABListCallback<?> list;
	
	@Override
	public Integer callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return list.callback(game, caster, localStore, castId).size();
	}

}
