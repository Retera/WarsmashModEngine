package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.build;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityRepair;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveAndTargetableVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CBehaviorRepair extends CAbstractRangedBehavior {
	private final CAbilityRepair ability;
	private final AbilityTargetStillAliveAndTargetableVisitor stillAliveVisitor;

	private int nextChargeTick = 0;

	public CBehaviorRepair(final CUnit unit, final CAbilityRepair ability) {
		super(unit);
		this.ability = ability;
		this.stillAliveVisitor = new AbilityTargetStillAliveAndTargetableVisitor();
	}

	public CBehavior reset(CSimulation game, final CWidget target) {
		this.nextChargeTick = 0;
		return innerReset(game, target, false);
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		float castRange = this.ability.getCastRange();
		if (this.target instanceof CUnit) {
			final CUnit unitTarget = (CUnit) this.target;
			if (unitTarget.getMovementType() == PathingGrid.MovementType.FLOAT) {
				castRange += this.ability.getNavalRangeBonus();
			}
		}
		return this.unit.canReach(this.target, castRange);
	}

	@Override
	protected CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {
		this.unit.getUnitAnimationListener().playAnimation(false, AnimationTokens.PrimaryTag.STAND, SequenceUtils.WORK,
				1.0f, true);
		if (this.target instanceof CWidget) {
			final CWidget targetWidget = (CWidget) this.target;
			if (this.target instanceof CUnit) {
				final CUnit taru = (CUnit) this.target;
				if (this.nextChargeTick == 0 || simulation.getGameTurnTick() >= this.nextChargeTick) {
					int goldCost = 0;
					int lumberCost = 0;
					if (!simulation.getPlayer(this.unit.getPlayerIndex()).charge(goldCost, lumberCost)) {
						return this.unit.pollNextOrderBehavior(simulation);
					}
					if (this.nextChargeTick == 0) {
						this.nextChargeTick = (int) (simulation.getGameTurnTick()
								+ this.ability.getRepairTimeRatio() / WarsmashConstants.SIMULATION_STEP_TIME);
					} else {
						this.unit.fireBehaviorChangeEvent(simulation, this, true);
					}
				}
			}
			float newLifeValue = targetWidget.getLife() + 1;
			final boolean done = newLifeValue > targetWidget.getMaxLife();
			if (done) {
				newLifeValue = targetWidget.getMaxLife();
			}
			targetWidget.setLife(simulation, newLifeValue);
			if (done) {
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
		return this.target.visit(this.stillAliveVisitor.reset(simulation, this.unit, this.ability.getTargetsAllowed()));
	}

	@Override
	protected void resetBeforeMoving(final CSimulation simulation) {
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

	@Override
	public int getHighlightOrderId() {
		return OrderIds.repair;
	}

	@Override
	public boolean interruptable() {
		return true;
	}

	@Override
	public CBehaviorCategory getBehaviorCategory() {
		return CBehaviorCategory.SPELL;
	}
}
