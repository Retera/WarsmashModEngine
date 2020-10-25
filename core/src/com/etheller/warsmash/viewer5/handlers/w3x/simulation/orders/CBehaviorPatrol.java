package com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders;

import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;

public class CBehaviorPatrol implements CBehavior {
	private final CUnit unit;
	private final int orderId;
	private final CWidget target;
	private CBehavior moveOrder;

	public CBehaviorPatrol(final CUnit unit, final int orderId, final CUnit target) {
		this.unit = unit;
		this.orderId = orderId;
		this.target = target;
		createMoveOrder(unit, target);
	}

	private void createMoveOrder(final CUnit unit, final CUnit target) {
		if (!unit.isMovementDisabled()) { // TODO: Check mobility instead
			if ((target instanceof CUnit) && !(target.getUnitType().isBuilding())) {
				this.moveOrder = new CBehaviorMove(unit, this.orderId, target);
			}
			else {
				this.moveOrder = new CBehaviorMove(unit, this.orderId, target.getX(), target.getY());
			}
		}
		else {
			this.moveOrder = null;
		}
	}

	@Override
	public boolean update(final CSimulation simulation) {
		if (this.target.isDead()) {
			return true;
		}
		final float range = this.unit.getAcquisitionRange();
		if (!this.unit.canReach(this.target, range)) {
			if (this.moveOrder == null) {
				return true;
			}
			if (this.moveOrder.update(simulation)) {
				return true; // we just cant reach them
			}
			return false;
		}
		if (!this.unit.isMovementDisabled()) {
			final float prevX = this.unit.getX();
			final float prevY = this.unit.getY();
			final float deltaY = this.target.getY() - prevY;
			final float deltaX = this.target.getX() - prevX;
			final double goalAngleRad = Math.atan2(deltaY, deltaX);
			float goalAngle = (float) Math.toDegrees(goalAngleRad);
			if (goalAngle < 0) {
				goalAngle += 360;
			}
			float facing = this.unit.getFacing();
			float delta = goalAngle - facing;
			final float turnRate = simulation.getUnitData().getTurnRate(this.unit.getTypeId());

			if (delta < -180) {
				delta = 360 + delta;
			}
			if (delta > 180) {
				delta = -360 + delta;
			}
			final float absDelta = Math.abs(delta);

			if ((absDelta <= 1.0) && (absDelta != 0)) {
				this.unit.setFacing(goalAngle);
			}
			else {
				float angleToAdd = Math.signum(delta) * (float) Math.toDegrees(turnRate);
				if (absDelta < Math.abs(angleToAdd)) {
					angleToAdd = delta;
				}
				facing += angleToAdd;
				this.unit.setFacing(facing);
			}
		}
		else {
		}
		this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f, false);
		return false;
	}

	@Override
	public int getOrderId() {
		return this.orderId;
	}

}
