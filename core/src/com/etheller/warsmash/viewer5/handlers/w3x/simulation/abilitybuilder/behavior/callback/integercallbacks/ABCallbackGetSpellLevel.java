package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABCallbackGetSpellLevel extends ABIntegerCallback {
	
	@Override
	public Integer callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return (int) localStore.get(ABLocalStoreKeys.CURRENTLEVEL);
	}

}
