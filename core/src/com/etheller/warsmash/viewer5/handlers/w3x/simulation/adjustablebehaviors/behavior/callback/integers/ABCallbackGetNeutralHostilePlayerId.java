package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetNeutralHostilePlayerId extends ABIntegerCallback {

	@Override
	public Integer callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return WarsmashConstants.MAX_PLAYERS - 4;
	}

}
