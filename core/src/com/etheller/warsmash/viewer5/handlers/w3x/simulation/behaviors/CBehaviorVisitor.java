package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.CBehaviorAbilityBuilderBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.CBehaviorAbilityBuilderNoTarget;

public interface CBehaviorVisitor<T> {
	T accept(CBehavior target);

	T accept(CRangedBehavior target);

	T accept(CBehaviorAbilityBuilderBase target);
	T accept(CBehaviorAbilityBuilderNoTarget target);

}
