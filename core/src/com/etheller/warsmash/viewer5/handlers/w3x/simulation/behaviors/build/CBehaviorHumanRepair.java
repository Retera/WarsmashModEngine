package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.build;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityHumanRepair;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveAndTargetableVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CBehaviorHumanRepair extends CAbstractRangedBehavior {
	private final CAbilityHumanRepair ability;
	private final AbilityTargetStillAliveAndTargetableVisitor stillAliveVisitor;

	private int nextNotifyTick = 0;

	private float totalGoldCharged = 0;
	private float totalLumberCharged = 0;

	private float goldPerUpdate = 0;
	private float lumberPerUpdate = 0;
	private float hpPerUpdate = 0;

	public CBehaviorHumanRepair(final CUnit unit, final CAbilityHumanRepair ability) {
		super(unit);
		this.ability = ability;
		this.stillAliveVisitor = new AbilityTargetStillAliveAndTargetableVisitor();
	}

	public CBehavior reset(CSimulation game, final CWidget target) {
		this.nextNotifyTick = 0;
		this.totalGoldCharged = 0;
		this.totalLumberCharged = 0;
		if (target instanceof CUnit) {
			final CUnit taru = (CUnit) target;
			if (taru.isConstructing() && taru.isConstructingPaused()) {
				this.goldPerUpdate = taru.getUnitType().getGoldRepairCost() * this.ability.getPowerBuildCostRatio()
						* WarsmashConstants.SIMULATION_STEP_TIME
						/ (taru.getUnitType().getBuildTime());
				this.lumberPerUpdate = taru.getUnitType().getLumberRepairCost() * this.ability.getPowerBuildCostRatio()
						* WarsmashConstants.SIMULATION_STEP_TIME
						/ (taru.getUnitType().getBuildTime());
				this.hpPerUpdate = taru.getMaxLife()
						* (WarsmashConstants.SIMULATION_STEP_TIME / taru.getUnitType().getBuildTime())
						* (1.0f - WarsmashConstants.BUILDING_CONSTRUCT_START_LIFE)
						* this.ability.getPowerBuildTimeRatio();
			} else {
				this.goldPerUpdate = taru.getUnitType().getGoldRepairCost()
						* (WarsmashConstants.SIMULATION_STEP_TIME / taru.getUnitType().getRepairTime())
						* this.ability.getRepairCostRatio() * this.ability.getRepairTimeRatio();
				this.lumberPerUpdate = taru.getUnitType().getLumberRepairCost()
						* (WarsmashConstants.SIMULATION_STEP_TIME / taru.getUnitType().getRepairTime())
						* this.ability.getRepairCostRatio() * this.ability.getRepairTimeRatio();
				this.hpPerUpdate = taru.getMaxLife()
						* (WarsmashConstants.SIMULATION_STEP_TIME / taru.getUnitType().getRepairTime())
						* this.ability.getRepairTimeRatio();
			}
		} else if (target instanceof CDestructable) {
			final CDestructable tard = (CDestructable) target;
			this.goldPerUpdate = tard.getDestType().getGoldRepairCost()
					* (WarsmashConstants.SIMULATION_STEP_TIME / tard.getDestType().getRepairTime())
					* this.ability.getRepairCostRatio() * this.ability.getRepairTimeRatio();
			this.lumberPerUpdate = tard.getDestType().getLumberRepairCost()
					* (WarsmashConstants.SIMULATION_STEP_TIME / tard.getDestType().getRepairTime())
					* this.ability.getRepairCostRatio() * this.ability.getRepairTimeRatio();
			this.hpPerUpdate = tard.getMaxLife()
					* (WarsmashConstants.SIMULATION_STEP_TIME / tard.getDestType().getRepairTime())
					* this.ability.getRepairTimeRatio();
		} else {
			this.goldPerUpdate = 0;
			this.lumberPerUpdate = 0;
			this.hpPerUpdate = 0;
		}
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
		if (this.target instanceof CWidget) {
			final CWidget targetWidget = (CWidget) this.target;
			if (targetWidget.getLife() >= targetWidget.getMaxLife()) {
				return this.unit.pollNextOrderBehavior(simulation);
			}
		}
		final CUnit targetUnit = this.target.visit(AbilityTargetVisitor.UNIT);
		if (targetUnit == null || !targetUnit.isConstructing() || !targetUnit.isConstructingPaused()
				|| targetUnit.getWorker() != this.unit) {
			final float ng = this.totalGoldCharged + this.goldPerUpdate + this.goldPerUpdate*this.ability.getPowerBuildTimeRatio()*targetUnit.getPowerWorkers();
			final float nl = this.totalLumberCharged + this.lumberPerUpdate + this.lumberPerUpdate*this.ability.getPowerBuildTimeRatio()*targetUnit.getPowerWorkers();
			final int gdx = (int) (ng) - (int) this.totalGoldCharged;
			final int ldx = (int) (nl) - (int) this.totalLumberCharged;
			this.totalGoldCharged = ng;
			this.totalLumberCharged = nl;
			if (!simulation.getPlayer(this.unit.getPlayerIndex()).charge(gdx, ldx)) {
				return this.unit.pollNextOrderBehavior(simulation);
			}
		}
		this.unit.getUnitAnimationListener().playAnimation(false, AnimationTokens.PrimaryTag.STAND, SequenceUtils.WORK,
				1.0f, true);
		if (this.nextNotifyTick == 0 || simulation.getGameTurnTick() >= this.nextNotifyTick) {
			if (this.nextNotifyTick == 0) {
				this.nextNotifyTick = (int) (simulation.getGameTurnTick()
						+ 0.5 / WarsmashConstants.SIMULATION_STEP_TIME);
			} else {
				this.unit.fireBehaviorChangeEvent(simulation, this, true);
			}
		}
		if (this.target instanceof CWidget) {
			final CWidget targetWidget = (CWidget) this.target;
			if ((targetUnit != null) && targetUnit.isConstructing() && targetUnit.isConstructingPaused()) {
				if (targetUnit.getWorker() == this.unit) {
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
				} else {
					targetUnit.setConstructionProgress(targetUnit.getConstructionProgress()
							+ WarsmashConstants.SIMULATION_STEP_TIME * this.ability.getPowerBuildTimeRatio());
					final int buildTime = targetUnit.getUnitType().getBuildTime();
					targetUnit.setLife(simulation,
							Math.min(targetUnit.getLife() + this.hpPerUpdate, targetUnit.getMaximumLife()));
					if (targetUnit.getConstructionProgress() >= buildTime) {
						return this.unit.pollNextOrderBehavior(simulation);
					}
				}

			} else {
				float newLifeValue = targetWidget.getLife() + this.hpPerUpdate;
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
		if (target instanceof CUnit) {
			final CUnit taru = (CUnit) target;
			if (taru.isConstructing() && taru.isConstructingPaused()) {
				if (taru.getWorker() == null) {
					taru.setWorker(this.unit, false);
				} else {
					taru.addPowerWorker();
				}
			}
		}
	}

	@Override
	public void end(final CSimulation game, final boolean interrupted) {
		if (target instanceof CUnit) {
			final CUnit taru = (CUnit) target;
			if (taru.isConstructing() && taru.isConstructingPaused()) {
				if (taru.getWorker() == this.unit) {
					taru.setWorker(null, false);
				} else {
					taru.removePowerWorker();
				}
			}

		}
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
