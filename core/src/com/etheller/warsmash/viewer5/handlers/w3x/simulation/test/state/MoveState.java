package com.etheller.warsmash.viewer5.handlers.w3x.simulation.test.state;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.test.IBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.test.IState;

public class MoveState implements IState {
	public IBehavior behavior;
	public CUnit unit;
	public float targetX;
	public float targetY;

	public MoveState reset(final IBehavior behavior, final CUnit unit, final float targetX, final float targetY) {
		this.behavior = behavior;
		this.unit = unit;
		this.targetX = targetX;
		this.targetY = targetY;
		return this;
	}

	@Override
	public void execute() {
		final float dx = this.targetX - this.unit.getX();
		final float dy = this.targetY - this.unit.getY();
		this.unit.setX(this.unit.getX(), collision);
	}

}
