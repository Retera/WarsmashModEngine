package com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.COrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityMove;

public class CMoveOrder implements COrder {
	private final CUnit unit;
	private final float targetX;
	private final float targetY;
	private boolean wasWithinPropWindow = false;

	public CMoveOrder(final CUnit unit, final float targetX, final float targetY) {
		this.unit = unit;
		this.targetX = targetX;
		this.targetY = targetY;
	}

	@Override
	public boolean update(final CSimulation simulation) {
		final float prevX = this.unit.getX();
		final float prevY = this.unit.getY();
		final float deltaY = this.targetY - prevY;
		final float deltaX = this.targetX - prevX;
		final double goalAngleRad = Math.atan2(deltaY, deltaX);
		float goalAngle = (float) Math.toDegrees(goalAngleRad);
		if (goalAngle < 0) {
			goalAngle += 360;
		}
		float facing = this.unit.getFacing();
		float delta = goalAngle - facing;
		final float propulsionWindow = simulation.getUnitData().getPropulsionWindow(this.unit.getTypeId());
		final float turnRate = simulation.getUnitData().getTurnRate(this.unit.getTypeId());
		final int speed = this.unit.getSpeed();

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
		if (absDelta < propulsionWindow) {
			final float speedTick = speed * WarsmashConstants.SIMULATION_STEP_TIME;
			final float speedTickSq = speedTick * speedTick;

			if (((deltaX * deltaX) + (deltaY * deltaY)) <= speedTickSq) {
				this.unit.setX(this.targetX);
				this.unit.setY(this.targetY);
				return true;
			}
			else {
				final double radianFacing = Math.toRadians(facing);
				this.unit.setX(prevX + (float) (Math.cos(radianFacing) * speedTick));
				this.unit.setY(prevY + (float) (Math.sin(radianFacing) * speedTick));
			}
			this.wasWithinPropWindow = true;
		}
		else {
			// If this happens, the unit is facing the wrong way, and has to turn before
			// moving.
			this.wasWithinPropWindow = false;
		}

		return false;
	}

	@Override
	public int getOrderId() {
		return CAbilityMove.ORDER_ID;
	}

	@Override
	public String getAnimationName() {
		if (!this.wasWithinPropWindow) {
			return "stand";
		}
		return "walk";
	}

}
