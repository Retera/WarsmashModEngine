package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.ABBehaviorAbilityBuilderBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.ABBehaviorAbilityBuilderNoTarget;

public class BehaviorAbilityVisitor implements CBehaviorVisitor<CAbility> {
	public static final BehaviorAbilityVisitor INSTANCE = new BehaviorAbilityVisitor();

	@Override
	public CAbility accept(CBehavior target) {
		return null;
	}

	@Override
	public CAbility accept(CRangedBehavior target) {
		return null;
	}

	@Override
	public CAbility accept(ABBehaviorAbilityBuilderBase target) {
		return target.getAbility();
	}

	@Override
	public CAbility accept(ABBehaviorAbilityBuilderNoTarget target) {
		return target.getAbility();
	}

	@Override
	public CAbility accept(CBehaviorMove target) {
		return null;
	}
	
	
}
