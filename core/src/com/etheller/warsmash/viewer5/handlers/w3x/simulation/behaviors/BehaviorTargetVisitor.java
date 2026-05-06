package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.ABBehaviorAbilityBuilderBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.ABBehaviorAbilityBuilderNoTarget;

public class BehaviorTargetVisitor implements CBehaviorVisitor<AbilityTarget> {
	public static final BehaviorTargetVisitor INSTANCE = new BehaviorTargetVisitor();

	@Override
	public AbilityTarget accept(CBehavior target) {
		return null;
	}

	@Override
	public AbilityTarget accept(CRangedBehavior target) {
		return target.getTarget();
	}

	@Override
	public AbilityTarget accept(ABBehaviorAbilityBuilderBase target) {
		return target.getTarget();
	}

	@Override
	public AbilityTarget accept(ABBehaviorAbilityBuilderNoTarget target) {
		return null;
	}

	@Override
	public AbilityTarget accept(CBehaviorMove target) {
		return target.getTarget();
	}
	
	
}
