package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unitlisteners.behavior.internalcallback;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.target.ABTargetCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.BehaviorTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;

public class ABCallbackGetNewBehaviorTarget extends ABTargetCallback {

	@Override
	public AbilityTarget callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		CBehavior beh = localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.POSTCHANGEBEHAVIOR, castId),
				CBehavior.class);
		AbilityTarget tar = beh.visit(BehaviorTargetVisitor.INSTANCE);

		return tar;
	}

}
