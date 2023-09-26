package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.destructablebuff;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CDestructableBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABCallbackGetLastCreatedDestructableBuff extends ABDestructableBuffCallback {

	@Override
	public CDestructableBuff callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return (CDestructableBuff) localStore.get(ABLocalStoreKeys.LASTCREATEDDESTBUFF);
	}

}
