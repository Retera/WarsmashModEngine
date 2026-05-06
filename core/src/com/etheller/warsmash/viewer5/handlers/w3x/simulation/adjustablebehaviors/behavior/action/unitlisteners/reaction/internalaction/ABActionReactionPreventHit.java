
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unitlisteners.reaction.internalaction;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABActionReactionPreventHit implements ABAction {

	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.REACTIONALLOWHIT, castId), false);
	}
}