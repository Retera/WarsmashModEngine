package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.abilitycallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABCallbackGetReactionAbility extends ABAbilityCallback {

	@Override
	public CAbility callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return (CAbility) localStore.get(ABLocalStoreKeys.REACTIONABILITY);
	}

}
