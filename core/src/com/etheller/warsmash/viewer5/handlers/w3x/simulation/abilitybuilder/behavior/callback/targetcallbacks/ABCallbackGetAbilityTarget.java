package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.targetcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABCallbackGetAbilityTarget extends ABTargetCallback {

	@Override
	public AbilityTarget callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		AbilityPointTarget target = (AbilityPointTarget) localStore.get(ABLocalStoreKeys.ABILITYTARGETEDLOCATION+castId);
		if (target != null) {
			return target;
		}
		CUnit taru = (CUnit) localStore.get(ABLocalStoreKeys.ABILITYTARGETEDUNIT+castId);
		if (taru != null) {
			return taru;
		}
		return null;
	}

}
