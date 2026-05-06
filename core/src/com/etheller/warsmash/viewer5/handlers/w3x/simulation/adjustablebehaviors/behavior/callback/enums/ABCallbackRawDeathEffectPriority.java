package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitDeathReplacementEffectPriority;

public class ABCallbackRawDeathEffectPriority extends ABDeathReplacementPriorityCallback {

	private CUnitDeathReplacementEffectPriority priority;
	
	@Override
	public CUnitDeathReplacementEffectPriority callback(CUnit caster, ABLocalDataStore localStore,
			final int castId) {
		return priority;
	}

}
