package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPreDamageListenerPriority;

public class ABCallbackRawPreDamageListenerPriority extends ABAttackPreDamageListenerPriorityCallback {

	private CUnitAttackPreDamageListenerPriority priority;
	
	@Override
	public CUnitAttackPreDamageListenerPriority callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return priority;
	}

}
