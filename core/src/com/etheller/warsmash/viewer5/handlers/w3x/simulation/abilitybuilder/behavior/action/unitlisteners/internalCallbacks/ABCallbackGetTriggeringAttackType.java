package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unitlisteners.internalCallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks.ABAttackTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;

public class ABCallbackGetTriggeringAttackType extends ABAttackTypeCallback {
	
	@Override
	public CAttackType callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return (CAttackType) localStore.get(ABLocalStoreKeys.ATTACKTYPE+castId);
	}

}
