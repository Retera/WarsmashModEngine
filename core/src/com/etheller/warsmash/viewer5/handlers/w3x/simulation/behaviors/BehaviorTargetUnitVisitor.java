package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.ABBehaviorAbilityBuilderBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.ABBehaviorAbilityBuilderNoTarget;

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

	@Override
	public CUnit accept(ABBehaviorAbilityBuilderBase target) {
		if (target.getTarget() != null) {
			return target.getTarget().visit(AbilityTargetVisitor.UNIT);
		}
		return null;
	}

	@Override
	public CUnit accept(ABBehaviorAbilityBuilderNoTarget target) {
		return null;
	}

	@Override
	public CUnit accept(CBehaviorMove target) {
		return null;
	}
	
	
}
