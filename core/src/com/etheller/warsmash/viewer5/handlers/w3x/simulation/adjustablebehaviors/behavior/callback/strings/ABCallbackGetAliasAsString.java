package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABCallbackGetAliasAsString extends ABStringCallback {

	@Override
	public String callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return (localStore.get(ABLocalStoreKeys.ALIAS, War3ID.class)).asStringValue();
	}

}
