package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitgroupcallbacks;

import java.util.Map;
import java.util.Set;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class ABCallbackGetUnitGroupByName extends ABUnitGroupCallback {

	private String name;

	@SuppressWarnings("unchecked")
	@Override
	public Set<CUnit> callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return (Set<CUnit>) localStore.get("_unitgroup_" + name);
	}
}
