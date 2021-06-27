package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.build;

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
        unit.getUnitAnimationListener().playAnimation(false, AnimationTokens.PrimaryTag.STAND,
                SequenceUtils.WORK, 1.0f, true);
        if(this.target instanceof CWidget) {
            CWidget targetWidget = (CWidget) this.target;
            float newLifeValue = targetWidget.getLife() + 1;
            boolean done = newLifeValue > targetWidget.getMaxLife();
            if(done) {
                newLifeValue = targetWidget.getMaxLife();
            }
            targetWidget.setLife(simulation, newLifeValue);
            if(done) {
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
