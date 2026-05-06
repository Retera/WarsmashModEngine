package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.ABBehaviorAbilityBuilderBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.ABBehaviorAbilityBuilderNoTarget;

public interface CBehaviorVisitor<T> {
	T accept(CBehavior target);

	T accept(CRangedBehavior target);

	T accept(ABBehaviorAbilityBuilderBase target);
	T accept(ABBehaviorAbilityBuilderNoTarget target);

	T accept(CBehaviorMove target);

}
