package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.destructable;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABCallbackGetAbilityTargetedDestructable extends ABDestructableCallback {

	@Override
	public CDestructable callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return localStore.get(ABLocalStoreKeys.ABILITYTARGETEDDESTRUCTABLE, CDestructable.class);
	}

}
