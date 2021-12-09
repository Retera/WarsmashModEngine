package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.harvest;

import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityWispHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.SimulationRenderComponent;

public class CBehaviorWispHarvest extends CAbstractRangedBehavior {
    private int lastIncomeTick;
    private final CAbilityWispHarvest abilityWispHarvest;
    private boolean harvesting = false;
    private SimulationRenderComponent spellEffectOverDestructable;

    public CBehaviorWispHarvest(CUnit unit, CAbilityWispHarvest abilityWispHarvest) {
        super(unit);
        this.abilityWispHarvest = abilityWispHarvest;
    }

    public CBehaviorWispHarvest reset(final CWidget target) {
        innerReset(target, false);
        return this;
    }

    @Override
    protected CBehavior update(CSimulation simulation, boolean withinFacingWindow) {
        if(target.getX() != unit.getX() || target.getY() != unit.getY()) {
            unit.setX(target.getX(), simulation.getWorldCollision(), simulation.getRegionManager());
            unit.setY(target.getY(), simulation.getWorldCollision(), simulation.getRegionManager());
            simulation.unitRepositioned(unit); // dont interpolate, instant jump
        }
        int gameTurnTick = simulation.getGameTurnTick();
        if (gameTurnTick - lastIncomeTick >= abilityWispHarvest.getPeriodicIntervalLengthTicks()) {
            lastIncomeTick = gameTurnTick;
            final CPlayer player = simulation.getPlayer(this.unit.getPlayerIndex());
            player.setLumber(player.getLumber() + this.abilityWispHarvest.getLumberPerInterval());
            simulation.unitGainResourceEvent(this.unit, ResourceType.LUMBER,
                    abilityWispHarvest.getLumberPerInterval());
        }
        if(!harvesting) {
            onStartHarvesting(simulation);
            harvesting = true;
        }
        return this;
    }

    private void onStartHarvesting(CSimulation simulation) {
        unit.getUnitAnimationListener().addSecondaryTag(AnimationTokens.SecondaryTag.LUMBER);
        simulation.unitLoopSoundEffectEvent(unit, abilityWispHarvest.getAlias());
        // TODO maybe use visitor instead of cast
        spellEffectOverDestructable = simulation.createSpellEffectOverDestructable(this.unit, (CDestructable) this.target, abilityWispHarvest.getAlias(), abilityWispHarvest.getArtAttachmentHeight());
        simulation.tagTreeOwned((CDestructable)target);
    }

    private void onStopHarvesting(CSimulation simulation) {
        unit.getUnitAnimationListener().removeSecondaryTag(AnimationTokens.SecondaryTag.LUMBER);
        simulation.unitStopSoundEffectEvent(unit, abilityWispHarvest.getAlias());
        simulation.untagTreeOwned((CDestructable)target);
        // TODO maybe use visitor instead of cast
        if(spellEffectOverDestructable != null) {
            spellEffectOverDestructable.remove();
            spellEffectOverDestructable = null;
        }
    }

    @Override
    protected CBehavior updateOnInvalidTarget(CSimulation simulation) {
        if (this.target instanceof CDestructable) {
            // wood
            if(harvesting) {
                onStopHarvesting(simulation);
                harvesting = false;
            }
            final CDestructable nearestTree = findNearestTree(this.unit, this.abilityWispHarvest,
                    simulation, this.unit);
            if (nearestTree != null) {
                return reset(nearestTree);
            }
        }
        return this.unit.pollNextOrderBehavior(simulation);
    }

    @Override
    protected boolean checkTargetStillValid(CSimulation simulation) {
        if(this.target instanceof  CDestructable) {
            if(!harvesting && simulation.isTreeOwned((CDestructable)this.target)) {
                return false;
            }
        }
        return this.target.visit(AbilityTargetStillAliveVisitor.INSTANCE);
    }

    @Override
    protected void resetBeforeMoving(CSimulation simulation) {
        if(harvesting) {
            onStopHarvesting(simulation);
            harvesting = false;
        }
    }

    @Override
    public boolean isWithinRange(CSimulation simulation) {
        return this.unit.canReach(this.target, 0);
    }

    @Override
    public void endMove(CSimulation game, boolean interrupted) {

    }

    @Override
    public void begin(CSimulation game) {

    }

    @Override
    public void end(CSimulation game, boolean interrupted) {
        if(harvesting) {
            onStopHarvesting(game);
            harvesting = false;
        }
    }

    @Override
    public int getHighlightOrderId() {
        return OrderIds.wispharvest;
    }

    public static CDestructable findNearestTree(final CUnit worker, final CAbilityWispHarvest abilityHarvest,
                                                final CSimulation simulation, final CWidget toObject) {
        CDestructable nearestMine = null;
        double nearestMineDistance = abilityHarvest.getCastRange()*abilityHarvest.getCastRange();
        for (final CDestructable unit : simulation.getDestructables()) {
            if (!unit.isDead()
                    && !simulation.isTreeOwned(unit)
                    && unit.canBeTargetedBy(simulation, worker, CAbilityWispHarvest.TREE_ALIVE_TYPE_ONLY)) {
                final double distance = unit.distanceSquaredNoCollision(toObject);
                if (distance < nearestMineDistance) {
                    nearestMineDistance = distance;
                    nearestMine = unit;
                }
            }
        }
        return nearestMine;
    }
}
