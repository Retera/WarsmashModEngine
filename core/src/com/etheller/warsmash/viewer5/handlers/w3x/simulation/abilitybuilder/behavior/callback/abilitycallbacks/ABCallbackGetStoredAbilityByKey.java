package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.abilitycallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;

public class ABCallbackGetStoredAbilityByKey extends ABAbilityCallback {

	private String key;
	
	@Override
	public CAbility callback(CSimulation game, CUnit caster, Map<String, Object> localStore) {
		return (CAbility) localStore.get(key);
	}

}
