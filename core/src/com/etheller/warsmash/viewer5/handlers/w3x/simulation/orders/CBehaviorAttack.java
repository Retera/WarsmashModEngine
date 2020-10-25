package com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;

public class CBehaviorAttack implements CBehavior {
	private final CUnit unit;
	private final int orderId;
	private boolean wasWithinPropWindow = false;
	private final CUnitAttack unitAttack;
	private final CWidget target;
	private int damagePointLaunchTime;
	private int backSwingTime;
	private CBehavior moveOrder;
	private int thisOrderCooldownEndTime;
	private boolean wasInRange = false;

	public CBehaviorAttack(final CUnit unit, final CUnitAttack unitAttack, final int orderId, final CWidget target) {
		this.unit = unit;
		this.unitAttack = unitAttack;
		this.orderId = orderId;
		this.target = target;
		createMoveOrder(unit, target);
	}

	private void createMoveOrder(final CUnit unit, final CWidget target) {
		if (!unit.isMovementDisabled()) { // TODO: Check mobility instead
			if ((target instanceof CUnit) && !(((CUnit) target).getUnitType().isBuilding())) {
				this.moveOrder = new CBehaviorMove(unit, this.orderId, (CUnit) target);
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
		if (this.target.isDead()
				|| !this.target.canBeTargetedBy(simulation, this.unit, this.unitAttack.getTargetsAllowed())) {
			return true;
		}
		float range = this.unitAttack.getRange();
		if ((this.target instanceof CUnit) && (((CUnit) this.target).getCurrentOrder() instanceof CBehaviorMove)
				&& (this.damagePointLaunchTime != 0 /*
													 * only apply range motion buffer if they were already in range and
													 * attacked
													 */)) {
			range += this.unitAttack.getRangeMotionBuffer();
		}
		if (!this.unit.canReach(this.target, range)
				|| (this.unit.distance(this.target) < this.unit.getUnitType().getMinimumAttackRange())) {
			if (this.moveOrder == null) {
				return true;
			}
			if (this.moveOrder.update(simulation)) {
				return true; // we just cant reach them
			}
			this.wasInRange = false;
			this.damagePointLaunchTime = 0;
			this.thisOrderCooldownEndTime = 0;
			return false;
		}
		this.wasInRange = true;
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
			final float propulsionWindow = simulation.getGameplayConstants().getAttackHalfAngle();
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
			if (absDelta < propulsionWindow) {
				this.wasWithinPropWindow = true;
			}
			else {
				// If this happens, the unit is facing the wrong way, and has to turn before
				// moving.
				this.wasWithinPropWindow = false;
			}
		}
		else {
			this.wasWithinPropWindow = true;
		}

		final int cooldownEndTime = this.unit.getCooldownEndTime();
		final int currentTurnTick = simulation.getGameTurnTick();
		if (this.wasWithinPropWindow) {
			if (this.damagePointLaunchTime != 0) {
				if (currentTurnTick >= this.damagePointLaunchTime) {
					int minDamage = this.unitAttack.getMinDamage();
					final int maxDamage = Math.max(0, this.unitAttack.getMaxDamage());
					if (minDamage > maxDamage) {
						minDamage = maxDamage;
					}
					final int damage;
					if (maxDamage == 0) {
						damage = 0;
					}
					else if (minDamage == maxDamage) {
						damage = minDamage;
					}
					else {
						damage = simulation.getSeededRandom().nextInt(maxDamage - minDamage) + minDamage;
					}
					this.unitAttack.launch(simulation, this.unit, this.target, damage);
					this.damagePointLaunchTime = 0;
				}
			}
			else if (currentTurnTick >= cooldownEndTime) {
				final float cooldownTime = this.unitAttack.getCooldownTime();
				final float animationBackswingPoint = this.unitAttack.getAnimationBackswingPoint();
				final int a1CooldownSteps = (int) (cooldownTime / WarsmashConstants.SIMULATION_STEP_TIME);
				final int a1BackswingSteps = (int) (animationBackswingPoint / WarsmashConstants.SIMULATION_STEP_TIME);
				final int a1DamagePointSteps = (int) (this.unitAttack.getAnimationDamagePoint()
						/ WarsmashConstants.SIMULATION_STEP_TIME);
				this.unit.setCooldownEndTime(currentTurnTick + a1CooldownSteps);
				this.thisOrderCooldownEndTime = currentTurnTick + a1CooldownSteps;
				this.damagePointLaunchTime = currentTurnTick + a1DamagePointSteps;
				this.backSwingTime = currentTurnTick + a1DamagePointSteps + a1BackswingSteps;
				this.unit.getUnitAnimationListener().playAnimation(true, PrimaryTag.ATTACK, SequenceUtils.EMPTY, 1.0f,
						true);
				this.unit.getUnitAnimationListener().queueAnimation(PrimaryTag.STAND, SequenceUtils.READY, false);
			}
			else if ((currentTurnTick >= this.thisOrderCooldownEndTime)) {
				this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.READY, 1.0f,
						false);
			}
		}
		else {
			this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.READY, 1.0f,
					false);
		}

		return false;
	}

	@Override
	public int getOrderId() {
		return this.orderId;
	}

}
