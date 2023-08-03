package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.buffcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;

public class ABCallbackGetStoredBuffByKey extends ABBuffCallback {

	private String key;
	
	@Override
	public CBuff callback(CSimulation game, CUnit caster, Map<String, Object> localStore) {
		return (CBuff) localStore.get(key);
	}

}
