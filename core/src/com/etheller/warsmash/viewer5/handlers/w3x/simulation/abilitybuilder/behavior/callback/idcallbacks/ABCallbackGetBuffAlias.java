package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks;

import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.buffcallbacks.ABBuffCallback;

public class ABCallbackGetBuffAlias extends ABIDCallback {
	
	private ABBuffCallback buff;
	
	@Override
	public War3ID callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return buff.callback(game, caster, localStore, castId).getAlias();
	}

}
