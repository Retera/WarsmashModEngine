package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.listener;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.listener.ABAbilityEffectReactionListener;

public class ABCallbackGetLastCreatedAbilityEffectReactionListener extends ABAbilityEffectReactionListenerCallback {

	@Override
	public ABAbilityEffectReactionListener callback(CUnit caster, ABLocalDataStore localStore,
			final int castId) {
		return localStore.get(ABLocalStoreKeys.LASTCREATEDAbERL, ABAbilityEffectReactionListener.class);
	}

}
