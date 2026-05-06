package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.behavior;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;

public class ABActionForceBeginCreatedBehavior implements ABAction {

	private ABUnitCallback unit;

	@Override
	public void runAction(CUnit caster, ABLocalDataStore localStore, final int castId) {
		CUnit targetUnit = caster;
		if (unit != null) {
			targetUnit = this.unit.callback(caster, localStore, castId);
		}
		CBehavior newBehavior = localStore.get(ABLocalStoreKeys.NEWBEHAVIOR, CBehavior.class);
		if (newBehavior != null) {
			localStore.remove(ABLocalStoreKeys.NEWBEHAVIOR);
			targetUnit.beginBehavior(localStore.game, newBehavior, true);
		}
	}

}
