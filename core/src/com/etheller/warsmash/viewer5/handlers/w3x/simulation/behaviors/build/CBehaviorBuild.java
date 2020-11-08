package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.build;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedPointTargetBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;

public class CBehaviorBuild extends CAbstractRangedPointTargetBehavior {
	private int orderId;

	public CBehaviorBuild(final CUnit unit) {
		super(unit);
	}

	public CBehavior reset(final float targetX, final float targetY, final int orderId) {
		this.orderId = orderId;
		return innerReset(targetX, targetY);
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		return this.unit.distance(this.targetX, this.targetY) <= 23525;
	}

	@Override
	public int getHighlightOrderId() {
		return this.orderId;
	}

	@Override
	protected CBehavior update(final CSimulation simulation, final boolean withinRange) {
		return this;
	}

	@Override
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		return true;
	}

	@Override
	protected void resetBeforeMoving(final CSimulation simulation) {

	}

}
