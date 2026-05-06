package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.ABBehaviorAbilityBuilderBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.ABBehaviorAbilityBuilderNoTarget;

public class BehaviorNextBehaviorVisitor implements CBehaviorVisitor<CRangedBehavior> {
	public static final BehaviorNextBehaviorVisitor INSTANCE = new BehaviorNextBehaviorVisitor();

	@Override
	public CRangedBehavior accept(CBehavior target) {
		return null;
	}

	@Override
	public CRangedBehavior accept(CBehaviorMove target) {
		return target.getRangedBehavior();
	}

	@Override
	public CRangedBehavior accept(CRangedBehavior target) {
		return null;
	}

	@Override
	public CRangedBehavior accept(ABBehaviorAbilityBuilderBase target) {
		return null;
	}

	@Override
	public CRangedBehavior accept(ABBehaviorAbilityBuilderNoTarget target) {
		return null;
	}
	
	
}
