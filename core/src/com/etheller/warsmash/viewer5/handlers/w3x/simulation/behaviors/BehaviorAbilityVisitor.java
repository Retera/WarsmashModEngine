package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.CBehaviorAbilityBuilderBase;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.CBehaviorAbilityBuilderNoTarget;

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
	public CAbility accept(CBehaviorAbilityBuilderBase target) {
		return target.getAbility();
	}

	@Override
	public CAbility accept(CBehaviorAbilityBuilderNoTarget target) {
		return target.getAbility();
	}
	
	
}
