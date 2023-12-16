package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.locationcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABCallbackGetProjectileCurrentLocation extends ABLocationCallback {

	@Override
	public AbilityPointTarget callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return (AbilityPointTarget) localStore.get(ABLocalStoreKeys.PROJECTILECURRENTLOC+castId);
	}

}
