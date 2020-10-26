package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;

public class CBehaviorAttack extends CAbstractRangedBehavior {

	public CBehaviorAttack(final CUnit unit) {
		super(unit);
	}

	private CUnitAttack unitAttack;
	private int damagePointLaunchTime;
	private int backSwingTime;
	private int thisOrderCooldownEndTime;

	public CBehaviorAttack reset(final CUnitAttack unitAttack, final CWidget target) {
		super.innerReset(target);
		this.unitAttack = unitAttack;
		this.damagePointLaunchTime = 0;
		this.backSwingTime = 0;
		this.thisOrderCooldownEndTime = 0;
		return this;
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		float range = this.unitAttack.getRange();
		if ((this.target instanceof CUnit) && (((CUnit) this.target).getCurrentBehavior() instanceof CBehaviorMove)
				&& (this.damagePointLaunchTime != 0 /*
													 * only apply range motion buffer if they were already in range and
													 * attacked
													 */)) {
			range += this.unitAttack.getRangeMotionBuffer();
		}
		return this.unit.canReach(this.target, range)
				&& (this.unit.distance(this.target) >= this.unit.getUnitType().getMinimumAttackRange());
	}

	@Override
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		return !this.target.isDead()
				&& this.target.canBeTargetedBy(simulation, this.unit, this.unitAttack.getTargetsAllowed());
	}

	@Override
	protected void resetBeforeMoving(final CSimulation simulation) {
		this.damagePointLaunchTime = 0;
		this.thisOrderCooldownEndTime = 0;
	}

	@Override
	public CBehavior update(final CSimulation simulation, final boolean withinRange) {
		final int cooldownEndTime = this.unit.getCooldownEndTime();
		final int currentTurnTick = simulation.getGameTurnTick();
		if (withinRange) {
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

		return this;
	}

}
