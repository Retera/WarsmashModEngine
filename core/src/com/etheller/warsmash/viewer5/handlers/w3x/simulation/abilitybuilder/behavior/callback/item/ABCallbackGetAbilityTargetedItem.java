package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.item;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABCallbackGetAbilityTargetedItem extends ABItemCallback {

	@Override
	public CItem callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return (CItem) localStore.get(ABLocalStoreKeys.ABILITYTARGETEDITEM+castId);
	}

}
