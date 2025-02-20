package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.root;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.nightelf.root.CAbilityRoot;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CBehaviorRoot extends CAbstractRangedBehavior {
	private final CAbilityRoot abilityRoot;
	private int rootStartTick;
	private int rootFinishTick;

	public CBehaviorRoot(final CUnit unit, final CAbilityRoot abilityRoot) {
		super(unit);
		this.abilityRoot = abilityRoot;
	}

	public CBehavior reset(final CSimulation game, final AbilityPointTarget pointTarget) {
		this.rootStartTick = -1;
		this.rootFinishTick = -1;
		return this.innerReset(game, pointTarget);
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
//		return ((AbilityPointTarget)target).dst2(unit.getX(), unit.getY()) <= 0.1;
		return this.unit.canReach(this.target.getX(), this.target.getY(), 0);
	}

	@Override
	public void endMove(final CSimulation game, final boolean interrupted) {
	}

	@Override
	public void begin(final CSimulation game) {
	}

	@Override
	public void end(final CSimulation game, final boolean interrupted) {
	}

	@Override
	public int getHighlightOrderId() {
		return OrderIds.root;
	}

	@Override
	protected CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {
		final float duration = this.abilityRoot.getDuration();
		if (this.rootStartTick == -1) {
			// plus one half sec of animation cheese, short delay
			this.unit.setPoint(this.target.getX(), this.target.getY(), simulation.getWorldCollision(),
					simulation.getRegionManager());
			this.rootStartTick = simulation.getGameTurnTick()
					+ (int) (duration / WarsmashConstants.SIMULATION_STEP_TIME);
		}
		else if (simulation.getGameTurnTick() >= this.rootStartTick) {
			if (this.rootFinishTick == -1) {
				this.unit.setFacing(simulation.getGameplayConstants().getRootAngle());
				this.abilityRoot.setRooted(true, this.unit, simulation);
				this.rootFinishTick = simulation.getGameTurnTick()
						+ (int) (duration / WarsmashConstants.SIMULATION_STEP_TIME);
				this.unit.setAcceptingOrders(false);
				this.unit.getUnitAnimationListener().playAnimationWithDuration(true, PrimaryTag.MORPH,
						SequenceUtils.EMPTY, duration, true);
			}
			else if (simulation.getGameTurnTick() >= this.rootFinishTick) {
				this.unit.getUnitAnimationListener().addSecondaryTag(SecondaryTag.ALTERNATE);
				this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f,
						true);
				this.unit.setAcceptingOrders(true);
				return this.unit.pollNextOrderBehavior(simulation);
			}
		}

		return this;
	}

	@Override
	protected CBehavior updateOnInvalidTarget(final CSimulation simulation) {
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		return true;
	}

	@Override
	protected void resetBeforeMoving(final CSimulation simulation) {
	}

	@Override
	public boolean interruptable() {
		return true;
	}

	@Override
	public CBehaviorCategory getBehaviorCategory() {
		return CBehaviorCategory.MOVEMENT;
	}

}
