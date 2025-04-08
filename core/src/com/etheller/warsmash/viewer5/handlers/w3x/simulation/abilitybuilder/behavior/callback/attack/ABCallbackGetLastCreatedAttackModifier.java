package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.attack;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.listener.ABAttackModifier;

public class ABCallbackGetLastCreatedAttackModifier extends ABAttackModifierCallback {

	@Override
	public ABAttackModifier callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return (ABAttackModifier) localStore.get(ABLocalStoreKeys.LASTCREATEDAMod);
	}

}
