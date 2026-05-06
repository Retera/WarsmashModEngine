package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.destructable;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CDestructableBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.destructable.ABDestructableCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABConditionDoesDestructableHaveBuffMatchingCondition extends ABBooleanCallback {

	private ABDestructableCallback dest;
	private ABBooleanCallback condition;

	@Override
	public Boolean callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		CDestructable theDestructable = dest.callback(caster, localStore, castId);
		if (theDestructable != null) {
			for (CDestructableBuff ability : theDestructable.getBuffs()) {
				localStore.put(ABLocalStoreKeys.MATCHINGDESTBUFF, ability);
				if (condition.callback(caster, localStore, castId)) {
					localStore.remove(ABLocalStoreKeys.MATCHINGDESTBUFF);
					return true;
				}
				localStore.remove(ABLocalStoreKeys.MATCHINGDESTBUFF);
			}
		}
		return false;
	}

}
