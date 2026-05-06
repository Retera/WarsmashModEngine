package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.longs;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.player.ABPlayerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackPlayerMaskIncludePlayers extends ABLongCallback {

	List<ABPlayerCallback> players;
	
	@Override
	public Long callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		long dat = 0;
		for (ABPlayerCallback player : players) {
			dat |= 1 << player.callback(caster, localStore, castId).getId();
		}
		
		return dat;
	}

}
