package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.longcallbacks;

import java.util.Map;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class ABCallbackPlayerMaskAllPlayers extends ABLongCallback {

	@Override
	public Long callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		long dat = 0;
		for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
			dat |= 1 << i;
		}
		
		return dat;
	}

}
