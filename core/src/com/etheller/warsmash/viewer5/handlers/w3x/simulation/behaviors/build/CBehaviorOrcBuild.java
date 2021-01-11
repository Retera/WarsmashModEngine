package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.build;

import java.awt.image.BufferedImage;
import java.util.EnumSet;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityBuildInProgress;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CBuildingPathingType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;

public class CBehaviorOrcBuild extends CAbstractRangedBehavior {
	private int highlightOrderId;
	private War3ID orderId;

	public CBehaviorOrcBuild(final CUnit unit) {
		super(unit);
	}

	public CBehavior reset(final AbilityPointTarget target, final int orderId, final int highlightOrderId) {
		this.highlightOrderId = highlightOrderId;
		this.orderId = new War3ID(orderId);
		return innerReset(target);
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		final CUnitType unitType = simulation.getUnitData().getUnitType(this.orderId);
		return this.unit.canReachToPathing(0, simulation.getGameplayConstants().getBuildingAngle(),
				unitType.getBuildingPathingPixelMap(), this.target.getX(), this.target.getY());
	}

	@Override
	public int getHighlightOrderId() {
		return this.highlightOrderId;
	}

	@Override
	protected CBehavior update(final CSimulation simulation, final boolean withinRange) {
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
		if (!buildLocationObstructed) {
			final CUnit constructedStructure = simulation.createUnit(this.orderId, this.unit.getPlayerIndex(),
					this.target.getX(), this.target.getY(), simulation.getGameplayConstants().getBuildingAngle());
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
			simulation.unitConstructedEvent(this.unit, constructedStructure);
		}
		else {
			final CPlayer player = simulation.getPlayer(this.unit.getPlayerIndex());
			player.setFoodUsed(player.getFoodUsed() - unitTypeToCreate.getFoodUsed());
			simulation.getCommandErrorListener().showCantPlaceError();
		}
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		return true;
	}

	@Override
	protected void resetBeforeMoving(final CSimulation simulation) {

	}

}
