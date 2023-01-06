package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class ABCallbackGetStoredUnitByKey extends ABUnitCallback {

	private String key;
	
	@Override
	public CUnit callback(CSimulation game, CUnit caster, Map<String, Object> localStore) {
		return (CUnit) localStore.get(key);
	}

}
