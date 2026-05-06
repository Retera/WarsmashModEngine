package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.behavior;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.BehaviorTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;

public class ABActionAttemptToReOrderPreviousBehavior implements ABAction {

	private ABBooleanCallback checkForOrders;

	@Override
	public void runAction(CUnit caster, ABLocalDataStore localStore, int castId) {
		CBehavior b = localStore.get(ABLocalStoreKeys.PREVIOUSBEHAVIOR, CBehavior.class);
		if (b != null && b != caster.getCurrentBehavior()) {
			if (checkForOrders == null || checkForOrders.callback(caster, localStore, castId)) {
				if (caster.getOrderQueue().isEmpty()) {
					if (caster.order(localStore.game, b.getHighlightOrderId(), b.visit(BehaviorTargetVisitor.INSTANCE))
							&& caster.getOrderQueue().isEmpty()) {
						localStore.put(ABLocalStoreKeys.NEWBEHAVIOR, caster.getCurrentBehavior());
					}
				}
			} else {
				if (caster.order(localStore.game, b.getHighlightOrderId(), b.visit(BehaviorTargetVisitor.INSTANCE))
						&& caster.getOrderQueue().isEmpty()) {
					localStore.put(ABLocalStoreKeys.NEWBEHAVIOR, caster.getCurrentBehavior());
				}
			}
		}
	}

}
