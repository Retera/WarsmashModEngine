package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.target;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABCallbackGetAbilityTarget extends ABTargetCallback {

	@Override
	public AbilityTarget callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		AbilityPointTarget target = localStore.get(
				ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDLOCATION, castId),
				AbilityPointTarget.class);
		if (target != null) {
			return target;
		}
		CUnit taru = localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ABILITYTARGETEDUNIT, castId),
				CUnit.class);
		if (taru != null) {
			return taru;
		}
		return null;
	}

}
