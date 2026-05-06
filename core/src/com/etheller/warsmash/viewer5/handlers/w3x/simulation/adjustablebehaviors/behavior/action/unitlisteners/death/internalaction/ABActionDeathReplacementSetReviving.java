
package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unitlisteners.death.internalaction;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitDeathReplacementResult;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners.CUnitDeathReplacementStacking;

public class ABActionDeathReplacementSetReviving implements ABAction {

	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		CUnitDeathReplacementResult result = localStore.get(
				ABLocalStoreKeys.combineKey(ABLocalStoreKeys.DEATHRESULT, castId), CUnitDeathReplacementResult.class);
		CUnitDeathReplacementStacking stacking = localStore.get(
				ABLocalStoreKeys.combineKey(ABLocalStoreKeys.DEATHSTACKING, castId),
				CUnitDeathReplacementStacking.class);

		stacking.setAllowStacking(false);
		stacking.setAllowSamePriorityStacking(false);
		result.setReviving(true);
	}
}