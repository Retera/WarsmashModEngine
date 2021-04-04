package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class AbilityTargetStillAliveVisitor implements AbilityTargetVisitor<Boolean> {
	public static final AbilityTargetStillAliveVisitor INSTANCE = new AbilityTargetStillAliveVisitor();

	@Override
	public Boolean accept(final AbilityPointTarget target) {
		return Boolean.TRUE;
	}

	@Override
	public Boolean accept(final CUnit target) {
		return !target.isDead() && !target.isHidden();
	}

	@Override
	public Boolean accept(final CDestructable target) {
		return !target.isDead();
	}

	@Override
	public Boolean accept(final CItem target) {
		return !target.isDead() && !target.isHidden();
	}

}