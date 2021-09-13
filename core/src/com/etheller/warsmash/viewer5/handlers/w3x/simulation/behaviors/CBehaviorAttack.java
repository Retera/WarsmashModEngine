package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveAndTargetableVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;

public class CBehaviorAttack extends CAbstractRangedBehavior {

	private int highlightOrderId;
	private final AbilityTargetStillAliveAndTargetableVisitor abilityTargetStillAliveVisitor;

	public CBehaviorAttack(final CUnit unit) {
		super(unit);
		this.abilityTargetStillAliveVisitor = new AbilityTargetStillAliveAndTargetableVisitor();
	}

	private CUnitAttack unitAttack;
	private int damagePointLaunchTime;
	private int backSwingTime;
	private int thisOrderCooldownEndTime;
	private CBehaviorAttackListener attackListener;

	public CBehaviorAttack reset(final int highlightOrderId, final CUnitAttack unitAttack, final AbilityTarget target,
			final boolean disableMove, final CBehaviorAttackListener attackListener) {
		this.highlightOrderId = highlightOrderId;
		this.attackListener = attackListener;
		super.innerReset(target);
		this.unitAttack = unitAttack;
		this.damagePointLaunchTime = 0;
		this.backSwingTime = 0;
		this.thisOrderCooldownEndTime = 0;
		setDisableMove(disableMove);
		return this;
	}

	@Override
	public int getHighlightOrderId() {
		return this.highlightOrderId;
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		float range = this.unitAttack.getRange();
		if (simulation.getGameTurnTick() < this.unit.getCooldownEndTime()) {
			range += this.unitAttack.getRangeMotionBuffer();
		}
		return this.unit.canReach(this.target, range)
				&& (this.unit.distance(this.target) >= this.unit.getUnitType().getMinimumAttackRange());
	}

	@Override
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		return this.target.visit(
				this.abilityTargetStillAliveVisitor.reset(simulation, this.unit, this.unitAttack.getTargetsAllowed()));
	}

	@Override
	protected void resetBeforeMoving(final CSimulation simulation) {
		this.damagePointLaunchTime = 0;
		this.thisOrderCooldownEndTime = 0;
	}

	@Override
	protected CBehavior updateOnInvalidTarget(final CSimulation simulation) {
		return this.attackListener.onFinish(simulation, this.unit);
	}

	@Override
	public CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {
		final int cooldownEndTime = this.unit.getCooldownEndTime();
		final int currentTurnTick = simulation.getGameTurnTick();
		if (withinFacingWindow) {
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
					this.unitAttack.launch(simulation, this.unit, this.target, damage, this.attackListener);
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
				this.unit.getUnitAnimationListener().playAnimationWithDuration(true, PrimaryTag.ATTACK,
						SequenceUtils.EMPTY, animationBackswingPoint + this.unitAttack.getAnimationDamagePoint(), true);
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

		if ((this.backSwingTime != 0) && (currentTurnTick >= this.backSwingTime)) {
			this.backSwingTime = 0;
			return this.attackListener.onFirstUpdateAfterBackswing(this);
		}
		return this;
	}

	@Override
	public void begin(final CSimulation game) {

	}

	@Override
	public void end(final CSimulation game, final boolean interrupted) {
	}

	@Override
	public void endMove(final CSimulation game, final boolean interrupted) {

	}

}
