package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.destructable;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABCallbackGetAbilityTargetedDestructable extends ABDestructableCallback {

	@Override
	public CDestructable callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return (CDestructable) localStore.get(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE);
	}

}
