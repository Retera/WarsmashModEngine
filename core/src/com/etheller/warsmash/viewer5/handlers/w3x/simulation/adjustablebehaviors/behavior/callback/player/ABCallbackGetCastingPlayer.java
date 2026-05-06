package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.player;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class ABCallbackGetCastingPlayer extends ABPlayerCallback {
	
	@Override
	public CPlayer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return localStore.originPlayer;
	}

}
