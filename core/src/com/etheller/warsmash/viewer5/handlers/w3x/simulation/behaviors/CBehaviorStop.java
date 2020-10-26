package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class CBehaviorStop implements CBehavior {

	private final CUnit unit;

	public CBehaviorStop(final CUnit unit) {
		this.unit = unit;
	}

	@Override
	public CBehavior update(final CSimulation game) {
		this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f, true);
		return this.unit.pollNextOrderBehavior(game);
	}

}
