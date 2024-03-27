package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;

public class BehaviorTargetUnitVisitor implements CBehaviorVisitor<CUnit> {
	public static final BehaviorTargetUnitVisitor INSTANCE = new BehaviorTargetUnitVisitor();

	@Override
	public CUnit accept(CBehavior target) {
		return null;
	}

	@Override
	public CUnit accept(CRangedBehavior target) {
		if (target.getTarget() != null) {
			return target.getTarget().visit(AbilityTargetVisitor.UNIT);
		}
		return null;
	}
	
	
}
