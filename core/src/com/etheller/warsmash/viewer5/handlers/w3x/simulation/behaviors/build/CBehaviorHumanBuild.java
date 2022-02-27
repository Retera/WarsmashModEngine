package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.build;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityBuildInProgress;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityHumanRepair;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CBuildingPathingType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

import java.awt.image.BufferedImage;
import java.util.EnumSet;

public class CBehaviorHumanBuild extends CAbstractRangedBehavior {
    private static int delayAnimationTicks = (int) (2.267f / WarsmashConstants.SIMULATION_STEP_TIME);
    private int highlightOrderId;
    private War3ID orderId;
    private boolean unitCreated = false;
    private boolean done = false;
    private CUnit constructedStructure =  null;

    public CBehaviorHumanBuild(CUnit unit) {
        super(unit);
    }

    public CBehavior reset(final AbilityPointTarget target, final int orderId, final int highlightOrderId) {
        this.highlightOrderId = highlightOrderId;
        this.orderId = new War3ID(orderId);
        this.unitCreated = false;
        this.done = false;
        return innerReset(target);
    }

    @Override
    public boolean isWithinRange(final CSimulation simulation) {
        if (this.done != false) {
            return true;
        }
        final CUnitType unitType = simulation.getUnitData().getUnitType(this.orderId);
        final BufferedImage buildingPathingPixelMap = unitType.getBuildingPathingPixelMap();
        if (buildingPathingPixelMap == null) {
            return this.unit.canReach(this.target.getX(), this.target.getY(), unitType.getCollisionSize());
        }
        return this.unit.canReachToPathing(0, simulation.getGameplayConstants().getBuildingAngle(),
                buildingPathingPixelMap, this.target.getX(), this.target.getY());
    }

    @Override
    public int getHighlightOrderId() {
        return this.highlightOrderId;
    }

    @Override
    protected CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {
        if (this.done) {
                if(this.constructedStructure==null){
                    return this.unit.pollNextOrderBehavior(simulation);
                }else{
                    CBehaviorHumanRepair repair = new CBehaviorHumanRepair(this.unit,this.unit.getFirstAbilityOfType(CAbilityHumanRepair.class));
                    repair.reset(this.constructedStructure);
                    return repair;
                }
        }
        else if (!this.unitCreated) {
            this.unitCreated = true;
            final CUnitType unitTypeToCreate = simulation.getUnitData().getUnitType(this.orderId);
            final BufferedImage buildingPathingPixelMap = unitTypeToCreate.getBuildingPathingPixelMap();
            boolean buildLocationObstructed = false;
            if (buildingPathingPixelMap != null) {
                final EnumSet<CBuildingPathingType> preventedPathingTypes = unitTypeToCreate.getPreventedPathingTypes();
                final EnumSet<CBuildingPathingType> requiredPathingTypes = unitTypeToCreate.getRequiredPathingTypes();

                if (!simulation.getPathingGrid().checkPathingTexture(this.target.getX(), this.target.getY(),
                        (int) simulation.getGameplayConstants().getBuildingAngle(), buildingPathingPixelMap,
                        preventedPathingTypes, requiredPathingTypes, simulation.getWorldCollision(), this.unit)) {
                    buildLocationObstructed = true;
                }
            }
            final int playerIndex = this.unit.getPlayerIndex();
            if (!buildLocationObstructed) {
                constructedStructure = simulation.createUnit(this.orderId, playerIndex, this.target.getX(),
                        this.target.getY(), simulation.getGameplayConstants().getBuildingAngle());
                constructedStructure.setConstructing(true);
                constructedStructure.setLife(simulation,
                        constructedStructure.getMaximumLife() * WarsmashConstants.BUILDING_CONSTRUCT_START_LIFE);
                constructedStructure.setFoodUsed(unitTypeToCreate.getFoodUsed());
                constructedStructure.add(simulation,
                        new CAbilityBuildInProgress(simulation.getHandleIdAllocator().createId()));
                for (final CAbility ability : constructedStructure.getAbilities()) {
                    ability.visit(AbilityDisableWhileUnderConstructionVisitor.INSTANCE);
                }
                final float deltaX = this.unit.getX() - this.target.getX();
                final float deltaY = this.unit.getY() - this.target.getY();
                final float delta = (float) Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
                this.unit.setPoint(this.target.getX() + ((deltaX / delta) * unitTypeToCreate.getCollisionSize()),
                        this.target.getY() + ((deltaY / delta) * unitTypeToCreate.getCollisionSize()),
                        simulation.getWorldCollision(), simulation.getRegionManager());
                constructedStructure.setConstuctionProcessType(ConstructionFlag.REQURIE_REPAIR);
                simulation.unitRepositioned(this.unit);
                simulation.unitConstructedEvent(this.unit, constructedStructure);
                this.done = true;
            }
            else {
                final CPlayer player = simulation.getPlayer(playerIndex);
                refund(player, unitTypeToCreate);
                simulation.getCommandErrorListener(playerIndex).showCantPlaceError();
                return this.unit.pollNextOrderBehavior(simulation);
            }
        }
        this.unit.getUnitAnimationListener().playAnimation(false, AnimationTokens.PrimaryTag.STAND, SequenceUtils.WORK, 1.0f, true);
        return this;
    }

    @Override
    protected boolean checkTargetStillValid(final CSimulation simulation) {
        return true;
    }

    @Override
    protected CBehavior updateOnInvalidTarget(final CSimulation simulation) {
        return this.unit.pollNextOrderBehavior(simulation);
    }

    @Override
    protected void resetBeforeMoving(final CSimulation simulation) {

    }

    @Override
    public void begin(final CSimulation game) {

    }

    @Override
    public void end(final CSimulation game, final boolean interrupted) {
        if (!this.unitCreated && interrupted) {
            final CPlayer player = game.getPlayer(this.unit.getPlayerIndex());
            final CUnitType unitTypeToCreate = game.getUnitData().getUnitType(this.orderId);
            refund(player, unitTypeToCreate);
        }
    }

    private void refund(final CPlayer player, final CUnitType unitTypeToCreate) {
        player.setFoodUsed(player.getFoodUsed() - unitTypeToCreate.getFoodUsed());
        player.refundFor(unitTypeToCreate);
    }

    @Override
    public void endMove(final CSimulation game, final boolean interrupted) {
        if (!this.unitCreated && interrupted) {
            final CPlayer player = game.getPlayer(this.unit.getPlayerIndex());
            final CUnitType unitTypeToCreate = game.getUnitData().getUnitType(this.orderId);
            refund(player, unitTypeToCreate);
        }
    }
}
