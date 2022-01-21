package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.build;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityHumanRepair;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveAndTargetableVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CBehaviorHumanRepair extends CAbstractRangedBehavior {
	private final CAbilityHumanRepair ability;
	private final AbilityTargetStillAliveAndTargetableVisitor stillAliveVisitor;

	public CBehaviorHumanRepair(final CUnit unit, final CAbilityHumanRepair ability) {
		super(unit);
		this.ability = ability;
		this.stillAliveVisitor = new AbilityTargetStillAliveAndTargetableVisitor();
	}

	public CBehaviorHumanRepair reset(final CWidget target) {
		innerReset(target, false);
		return this;
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		float castRange = this.ability.getCastRange();
		if (this.target instanceof CUnit) {
			final CUnit unitTarget = (CUnit) this.target;
			if (unitTarget.getUnitType().getMovementType() == PathingGrid.MovementType.FLOAT) {
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
			if ((targetWidget instanceof CUnit) && ((CUnit) targetWidget).isConstructing()) {
				final CUnit targetUnit = (CUnit) targetWidget;
				targetUnit.setConstructionProgress(
						targetUnit.getConstructionProgress() + WarsmashConstants.SIMULATION_STEP_TIME);
				final int buildTime = targetUnit.getUnitType().getBuildTime();
				final float healthGain = (WarsmashConstants.SIMULATION_STEP_TIME / buildTime)
						* (targetUnit.getMaximumLife() * (1.0f - WarsmashConstants.BUILDING_CONSTRUCT_START_LIFE));
				targetUnit.setLife(simulation,
						Math.min(targetUnit.getLife() + healthGain, targetUnit.getMaximumLife()));
				if (targetUnit.getConstructionProgress() >= buildTime) {
					return this.unit.pollNextOrderBehavior(simulation);
				}
			}
			else {
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
}
