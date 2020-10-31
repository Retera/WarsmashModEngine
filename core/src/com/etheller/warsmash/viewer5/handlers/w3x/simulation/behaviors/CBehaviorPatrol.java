package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.badlogic.gdx.math.Vector2;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CBehaviorPatrol implements CRangedBehavior {

	private final CUnit unit;
	private Vector2 target;
	private Vector2 startPoint;

	public CBehaviorPatrol(final CUnit unit) {
		this.unit = unit;
	}

	public CBehavior reset(final Vector2 target) {
		this.target = target;
		this.startPoint = new Vector2(this.unit.getX(), this.unit.getY());
		return this;
	}

	@Override
	public int getHighlightOrderId() {
		return OrderIds.patrol;
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		return this.unit.distance(this.target.x, this.target.y) <= simulation.getGameplayConstants()
				.getCloseEnoughRange(); // TODO this is not how it was meant to be used
	}

	@Override
	public CBehavior update(final CSimulation simulation) {
		final Vector2 temp = this.target;
		this.target = this.startPoint;
		this.startPoint = temp;
		return this.unit.getMoveBehavior().reset(this.target.x, this.target.y, this);
	}

}
