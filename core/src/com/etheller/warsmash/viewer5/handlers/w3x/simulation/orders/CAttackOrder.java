package com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.COrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityAttack;

public class CAttackOrder implements COrder {
	private final CUnit unit;
	private boolean wasWithinPropWindow = false;
	private final CWidget target;

	public CAttackOrder(final CUnit unit, final CWidget target) {
		this.unit = unit;
		this.target = target;
	}

	@Override
	public boolean update(final CSimulation simulation) {
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
			this.wasWithinPropWindow = true;
		}
		else {
			// If this happens, the unit is facing the wrong way, and has to turn before
			// moving.
			this.wasWithinPropWindow = false;
		}

		final int cooldownEndTime = this.unit.getCooldownEndTime();
		final int currentTurnTick = simulation.getGameTurnTick();
		if (currentTurnTick >= cooldownEndTime) {
			final float a1Cooldown = simulation.getUnitData().getA1Cooldown(this.unit.getTypeId());
			final int a1CooldownSteps = (int) (a1Cooldown / WarsmashConstants.SIMULATION_STEP_TIME);
			this.unit.setCooldownEndTime(currentTurnTick + a1CooldownSteps);
			simulation.createProjectile(this.unit, 0, this.target);
		}

		return false;
	}

	@Override
	public int getOrderId() {
		return CAbilityAttack.ORDER_ID;
	}

	@Override
	public AnimationTokens.PrimaryTag getAnimationName() {
		return AnimationTokens.PrimaryTag.ATTACK;
	}

}
