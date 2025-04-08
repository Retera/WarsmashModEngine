package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.buffcallbacks.ABBuffCallback;

public class ABCallbackGetBuffHandleAsString extends ABStringCallback {
	
	private ABBuffCallback buff;
	
	@Override
	public String callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return "" + buff.callback(game, caster, localStore, castId).getHandleId();
	}

}
