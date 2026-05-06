package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.player.ABPlayerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackPlayerToStateModValue extends ABIntegerCallback {

	private ABPlayerCallback player;

	@Override
	public Integer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return 1 << player.callback(caster, localStore, castId).getId();
	}

}
