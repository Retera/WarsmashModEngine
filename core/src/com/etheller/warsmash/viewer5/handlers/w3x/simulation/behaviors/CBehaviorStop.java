package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CBehaviorStop implements CBehavior {

	private final CUnit unit;

	public CBehaviorStop(final CUnit unit) {
		this.unit = unit;
	}

	@Override
	public int getHighlightOrderId() {
		return OrderIds.stop;
	}

	@Override
	public CBehavior update(final CSimulation game) {
		if (this.unit.autoAcquireTargets(game, false)) {
			return this.unit.getCurrentBehavior();
		}
		return this.unit.pollNextOrderBehavior(game);
	}

	@Override
	public void begin(final CSimulation game) {
		if (!this.unit.isConstructingOrUpgrading()) {
			this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f,
					true);
		}
	}

	@Override
	public void end(final CSimulation game, final boolean interrupted) {

	}

	@Override
	public boolean interruptable() {
		return true;
	}

	@Override
	public <T> T visit(final CBehaviorVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	public CBehaviorCategory getBehaviorCategory() {
		return CBehaviorCategory.IDLE;
	}

}
