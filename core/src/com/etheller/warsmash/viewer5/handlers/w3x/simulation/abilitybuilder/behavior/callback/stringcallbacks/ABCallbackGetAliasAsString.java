package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks;

import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABCallbackGetAliasAsString extends ABStringCallback {

	@Override
	public String callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return ((War3ID) localStore.get(ABLocalStoreKeys.ALIAS)).asStringValue();
	}

}
