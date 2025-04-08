package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.CBehaviorAbilityBuilderBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.CBehaviorAbilityBuilderNoTarget;

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
	public CRangedBehavior accept(CBehaviorAbilityBuilderBase target) {
		return null;
	}

	@Override
	public CRangedBehavior accept(CBehaviorAbilityBuilderNoTarget target) {
		return null;
	}
	
	
}
