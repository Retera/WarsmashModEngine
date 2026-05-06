package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.player;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class ABCallbackGetOwnerOfUnit extends ABPlayerCallback {

	private ABUnitCallback unit;

	@Override
	public CPlayer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return localStore.game.getPlayer(unit.callback(caster, localStore, castId).getPlayerIndex());
	}

}
