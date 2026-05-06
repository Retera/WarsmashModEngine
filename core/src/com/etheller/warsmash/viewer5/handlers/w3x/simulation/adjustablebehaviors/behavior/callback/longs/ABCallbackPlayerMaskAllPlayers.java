package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.longs;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackPlayerMaskAllPlayers extends ABLongCallback {

	@Override
	public Long callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		long dat = 0;
		for (int i = 0; i < WarsmashConstants.MAX_PLAYERS; i++) {
			dat |= 1 << i;
		}
		
		return dat;
	}

}
