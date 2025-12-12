package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.longcallbacks;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.player.ABPlayerCallback;

public class ABCallbackPlayerMaskExcludePlayers extends ABLongCallback {

	List<ABPlayerCallback> players;
	
	@Override
	public Long callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		long dat = 0;
		for (ABPlayerCallback player : players) {
			dat |= 1 << player.callback(game, caster, localStore, castId).getId();
		}
		
		return ~dat;
	}

}
