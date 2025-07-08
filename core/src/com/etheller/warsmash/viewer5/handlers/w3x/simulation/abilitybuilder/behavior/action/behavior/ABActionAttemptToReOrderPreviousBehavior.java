package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.behavior;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.BehaviorTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;

public class ABActionAttemptToReOrderPreviousBehavior implements ABAction {

	private ABBooleanCallback checkForOrders;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, int castId) {
		CBehavior b = (CBehavior) localStore.get(ABLocalStoreKeys.PREVIOUSBEHAVIOR);
		if (b != null && b != caster.getCurrentBehavior()) {
			if (checkForOrders == null || checkForOrders.callback(game, caster, localStore, castId)) {
				if (caster.getOrderQueue().isEmpty()) {
					if (caster.order(game, b.getHighlightOrderId(), b.visit(BehaviorTargetVisitor.INSTANCE))
							&& caster.getOrderQueue().isEmpty()) {
						localStore.put(ABLocalStoreKeys.NEWBEHAVIOR, caster.getCurrentBehavior());
					}
				}
			} else {
				if (caster.order(game, b.getHighlightOrderId(), b.visit(BehaviorTargetVisitor.INSTANCE))
						&& caster.getOrderQueue().isEmpty()) {
					localStore.put(ABLocalStoreKeys.NEWBEHAVIOR, caster.getCurrentBehavior());
				}
			}
		}
	}

}
