package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.build;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.*;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityHumanRepair;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveAndTargetableVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest.CBehaviorHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class CBehaviorHumanRepair extends CAbstractRangedBehavior {
    private final CAbilityHumanRepair ability;
    private AbilityTargetStillAliveAndTargetableVisitor stillAliveVisitor;

    public CBehaviorHumanRepair(CUnit unit, CAbilityHumanRepair ability) {
        super(unit);
        this.ability = ability;
        stillAliveVisitor = new AbilityTargetStillAliveAndTargetableVisitor();
    }

    public CBehaviorHumanRepair reset(final CWidget target) {
        innerReset(target, false);
        return this;
    }

    @Override
    public boolean isWithinRange(CSimulation simulation) {
        float castRange = ability.getCastRange();
        if(target instanceof CUnit) {
            CUnit unitTarget = (CUnit)target;
            if(unitTarget.getUnitType().getMovementType() == PathingGrid.MovementType.FLOAT) {
                castRange += ability.getNavalRangeBonus();
            }
        }
        return unit.canReach(target, castRange);
    }

    @Override
    protected CBehavior update(CSimulation simulation, boolean withinFacingWindow) {
        if(this.target instanceof CWidget) {
            CWidget targetWidget = (CWidget) this.target;
            //progress construction here (MFROMAZ)
            if(targetWidget.getClass()==CUnit.class) {
                CUnit targetUnit = ((CUnit) targetWidget);
                final CPlayer player = simulation.getPlayer(unit.getPlayerIndex());
                float newLifeValue = targetWidget.getLife() +
                        ((WarsmashConstants.SIMULATION_STEP_TIME / (targetUnit.getUnitType().getBuildTime()*this.ability.getRepairTimeRatio()))
                                * (targetUnit.getMaxLife()));
                float costs_gold = ((WarsmashConstants.SIMULATION_STEP_TIME / (targetUnit.getUnitType().getBuildTime()*this.ability.getRepairTimeRatio()))
                        * (targetUnit.getUnitType().getGoldCost()*this.ability.getRepairCostRatio()));
                float costs_lumber = ((WarsmashConstants.SIMULATION_STEP_TIME / (targetUnit.getUnitType().getBuildTime()*this.ability.getRepairTimeRatio()))
                        * (targetUnit.getUnitType().getLumberCost()*this.ability.getRepairCostRatio()));

                unit.getUnitAnimationListener().playAnimation(false, AnimationTokens.PrimaryTag.STAND,
                        SequenceUtils.WORK, 1.0f, true);

                float healthGain = (WarsmashConstants.SIMULATION_STEP_TIME / targetUnit.getUnitType().getBuildTime())
                        * (targetUnit.getMaxLife() * (1.0f - WarsmashConstants.BUILDING_CONSTRUCT_START_LIFE)) * (ability.getRepairTimeRatio());

                if (targetUnit.getConstuctionProcessType() != null
                        && targetUnit.getConstuctionProcessType().equals(ConstructionFlag.REQURIE_REPAIR)
                        && targetUnit.isConstructing()
                        && targetUnit.getConstructionProgress() < targetUnit.getUnitType().getBuildTime()  /*targetUnit.getClassifications().contains(CUnitClassification.BUILDING) &&*/) {
                    float constructionProgressGain =  WarsmashConstants.SIMULATION_STEP_TIME;
                    healthGain = (WarsmashConstants.SIMULATION_STEP_TIME / targetUnit.getUnitType().getBuildTime())
                            * (targetUnit.getMaxLife() * (1.0f - WarsmashConstants.BUILDING_CONSTRUCT_START_LIFE));
                    if(targetUnit.isConstructionPowerBuilding()){
                        costs_gold = ((WarsmashConstants.SIMULATION_STEP_TIME / (targetUnit.getUnitType().getBuildTime()*this.ability.getPowerBuildTimeRatio()))
                                * (targetUnit.getUnitType().getGoldCost()*this.ability.getPowerBuildCostRatio()));
                        costs_lumber = ((WarsmashConstants.SIMULATION_STEP_TIME / (targetUnit.getUnitType().getBuildTime()*this.ability.getPowerBuildTimeRatio()))
                                * (targetUnit.getUnitType().getLumberCost()*this.ability.getPowerBuildCostRatio()));
                        constructionProgressGain =  WarsmashConstants.SIMULATION_STEP_TIME * this.ability.getPowerBuildTimeRatio();
                        healthGain = (WarsmashConstants.SIMULATION_STEP_TIME* ability.getPowerBuildTimeRatio() / targetUnit.getUnitType().getBuildTime())
                                * (targetUnit.getMaxLife() * (1.0f - WarsmashConstants.BUILDING_CONSTRUCT_START_LIFE));
                    }else{
                        costs_gold = 0.0f;
                        costs_lumber = 0.0f;
                        targetUnit.setConstructionPowerBuild(true);
                    }
                    if(costs_gold > player.getGold() || costs_lumber > player.getLumber()) {
                        return unit.pollNextOrderBehavior(simulation);
                    }
                    targetUnit.setConstructionProgress(targetUnit.getConstructionProgress() +constructionProgressGain);
                }

                newLifeValue = Math.min(targetUnit.getLife() + healthGain, targetUnit.getMaximumLife());
                if(costs_gold > player.getGold() || costs_lumber > player.getLumber()){
                    return unit.pollNextOrderBehavior(simulation);
                }
                boolean done = (newLifeValue >= targetWidget.getMaxLife());
                if (done) {
                    newLifeValue = targetWidget.getMaxLife();
                }
                player.charge(costs_gold,costs_lumber);
                targetWidget.setLife(simulation, newLifeValue);
                if (done && (targetUnit.getConstructionProgress()>= targetUnit.getUnitType().getBuildTime())||!targetUnit.isConstructing()) {
                    return unit.pollNextOrderBehavior(simulation);
                }
            }else{
                return unit.pollNextOrderBehavior(simulation);
            }
        }
        return this;
    }

    @Override
    protected CBehavior updateOnInvalidTarget(CSimulation simulation) {
        return unit.pollNextOrderBehavior(simulation);
    }

    @Override
    protected boolean checkTargetStillValid(CSimulation simulation) {
        return target.visit(stillAliveVisitor.reset(simulation, unit, ability.getTargetsAllowed()));
    }

    @Override
    protected void resetBeforeMoving(CSimulation simulation) {}

    @Override
    public void begin(CSimulation game) {}

    @Override
    public void end(CSimulation game, boolean interrupted) {}

    @Override
    public void endMove(CSimulation game, boolean interrupted) {}

    @Override
    public int getHighlightOrderId() {
        return OrderIds.repair;
    }
}
