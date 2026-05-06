package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitAttackPreDamageListenerPriority;

public class ABCallbackRawPreDamageListenerPriority extends ABAttackPreDamageListenerPriorityCallback {

	private CUnitAttackPreDamageListenerPriority priority;

	@Override
	public CUnitAttackPreDamageListenerPriority callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return priority;
	}

}
