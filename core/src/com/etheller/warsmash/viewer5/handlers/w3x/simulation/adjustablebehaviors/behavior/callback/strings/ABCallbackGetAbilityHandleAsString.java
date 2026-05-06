package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.ability.ABAbilityCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetAbilityHandleAsString extends ABStringCallback {

	private ABAbilityCallback ability;

	@Override
	public String callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return "" + ability.callback(caster, localStore, castId).getHandleId();
	}

}
