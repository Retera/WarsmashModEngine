package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.buffcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABCallbackGetLastCreatedBuff extends ABBuffCallback {

	@Override
	public CBuff callback(CSimulation game, CUnit caster, Map<String, Object> localStore) {
		return (CBuff) localStore.get(ABLocalStoreKeys.LASTCREATEDBUFF);
	}

}
