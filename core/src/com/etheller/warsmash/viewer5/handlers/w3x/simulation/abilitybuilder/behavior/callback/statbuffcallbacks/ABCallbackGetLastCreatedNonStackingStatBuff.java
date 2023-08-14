package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.statbuffcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;

public class ABCallbackGetLastCreatedNonStackingStatBuff extends ABNonStackingStatBuffCallback {

	@Override
	public NonStackingStatBuff callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		return (NonStackingStatBuff) localStore.get(ABLocalStoreKeys.LASTCREATEDNSSB);
	}

}
