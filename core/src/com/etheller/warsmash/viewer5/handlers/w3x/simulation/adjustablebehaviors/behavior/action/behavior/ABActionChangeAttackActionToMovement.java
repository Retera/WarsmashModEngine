package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.behavior;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.BehaviorNextBehaviorVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.BehaviorTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CRangedBehavior;

public class ABActionChangeAttackActionToMovement implements ABAction {

	private ABUnitCallback unit;

	@Override
	public void runAction(CUnit caster, ABLocalDataStore localStore, final int castId) {
		CUnit targetUnit = caster;
		if (unit != null) {
			targetUnit = this.unit.callback(caster, localStore, castId);
		}
		if (targetUnit.getCurrentBehavior().getBehaviorCategory() == CBehaviorCategory.ATTACK) {
			targetUnit.beginBehavior(localStore.game,
					targetUnit.getMoveBehavior().reset(targetUnit.getMoveBehavior().getHighlightOrderId(),
							targetUnit.getCurrentBehavior().visit(BehaviorTargetVisitor.INSTANCE)),
					true);
		} else if (targetUnit.getCurrentBehavior().getBehaviorCategory() == CBehaviorCategory.MOVEMENT) {
			CRangedBehavior next = targetUnit.getCurrentBehavior().visit(BehaviorNextBehaviorVisitor.INSTANCE);
			if (next != null && next.getBehaviorCategory() == CBehaviorCategory.ATTACK) {
				targetUnit.beginBehavior(localStore.game,
						targetUnit.getMoveBehavior().reset(targetUnit.getMoveBehavior().getHighlightOrderId(),
								next.visit(BehaviorTargetVisitor.INSTANCE)),
						true);
			}
		}
	}

}
