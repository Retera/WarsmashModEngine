package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public abstract class CAbstractRangedPointTargetBehavior extends CAbstractRangedBehavior {

	protected float targetX;
	protected float targetY;

	public CAbstractRangedPointTargetBehavior(final CUnit unit) {
		super(unit);
	}

	protected final CAbstractRangedBehavior innerReset(final float targetX, final float targetY) {
		this.targetX = targetX;
		this.targetY = targetY;
		return innerReset();
	}

	@Override
	protected float getTargetX() {
		return this.targetX;
	}

	@Override
	protected float getTargetY() {
		return this.targetY;
	}

	@Override
	protected CBehaviorMove setupMoveBehavior() {
		return this.unit.getMoveBehavior().reset(this.targetX, this.targetY, this);
	}

}
