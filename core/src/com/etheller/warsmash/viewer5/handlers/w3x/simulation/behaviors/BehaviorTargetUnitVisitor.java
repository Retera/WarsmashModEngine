package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.CBehaviorAbilityBuilderBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.CBehaviorAbilityBuilderNoTarget;

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
	public CUnit accept(CBehaviorAbilityBuilderBase target) {
		if (target.getTarget() != null) {
			return target.getTarget().visit(AbilityTargetVisitor.UNIT);
		}
		return null;
	}

	@Override
	public CUnit accept(CBehaviorAbilityBuilderNoTarget target) {
		return null;
	}
	
	
}
