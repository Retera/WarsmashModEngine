package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks;

import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class ABCallbackGetWar3IDFromString extends ABIDCallback {

	private String id;
	
	@Override
	public War3ID callback(CSimulation game, CUnit caster, Map<String, Object> localStore) {
		return War3ID.fromString(this.id);
	}

}
