package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.build;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityBuildInProgress;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CBuildingPathingType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

import java.awt.image.BufferedImage;
import java.util.EnumSet;

public class CBehaviorNightElfBuild extends CBehaviorOrcBuild{
    public CBehaviorNightElfBuild(CUnit unit) {
        super(unit);
    }

    @Override
    protected CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {
        if (!this.unitCreated) {
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
                final CUnit constructedStructure = simulation.createUnit(this.orderId, playerIndex, this.target.getX(),
                        this.target.getY(), simulation.getGameplayConstants().getBuildingAngle());
                constructedStructure.setConstructing(true);
                constructedStructure.setWorkerInside(this.unit);
                constructedStructure.setLife(simulation,
                        constructedStructure.getMaximumLife() * WarsmashConstants.BUILDING_CONSTRUCT_START_LIFE);
                constructedStructure.setFoodUsed(unitTypeToCreate.getFoodUsed());
                constructedStructure.add(simulation,
                        new CAbilityBuildInProgress(simulation.getHandleIdAllocator().createId()));
                for (final CAbility ability : constructedStructure.getAbilities()) {
                    ability.visit(AbilityDisableWhileUnderConstructionVisitor.INSTANCE);
                }
                this.unit.setHidden(true);
                this.unit.setPaused(true);
                this.unit.setInvulnerable(true);
                constructedStructure.setConstuctionProcessType(ConstructionFlag.CONSUME_WORKER);
                simulation.unitConstructedEvent(this.unit, constructedStructure);
            }
            else {
                final CPlayer player = simulation.getPlayer(playerIndex);
                refund(player, unitTypeToCreate);
                simulation.getCommandErrorListener(playerIndex).showCantPlaceError();
            }
        }
        return this.unit.pollNextOrderBehavior(simulation);
    }
}
