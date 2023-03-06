package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.build;

import java.awt.image.BufferedImage;
import java.util.EnumSet;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityBuildInProgress;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityGoldMinable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.mine.CAbilityOverlayedMine;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CBuildingPathingType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.BuildOnBuildingIntersector;

public class CBehaviorUndeadBuild extends CAbstractRangedBehavior {

	private static int delayAnimationTicks = (int) (2.267f / WarsmashConstants.SIMULATION_STEP_TIME);
	private int highlightOrderId;
	private War3ID orderId;
	private boolean unitCreated = false;
	private int doneTick = 0;
	private final BuildOnBuildingIntersector buildOnBuildingIntersector;

	public CBehaviorUndeadBuild(final CUnit unit) {
		super(unit);
		this.buildOnBuildingIntersector = new BuildOnBuildingIntersector();
	}

	public CBehavior reset(final AbilityPointTarget target, final int orderId, final int highlightOrderId) {
		this.highlightOrderId = highlightOrderId;
		this.orderId = new War3ID(orderId);
		this.unitCreated = false;
		this.doneTick = 0;
		return innerReset(target);
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		if (this.doneTick != 0) {
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
		if (this.doneTick != 0) {
			if (simulation.getGameTurnTick() > this.doneTick) {
				return this.unit.pollNextOrderBehavior(simulation);
			}
		}
		else if (!this.unitCreated) {
			this.unitCreated = true;
			final CUnitType unitTypeToCreate = simulation.getUnitData().getUnitType(this.orderId);
			final BufferedImage buildingPathingPixelMap = unitTypeToCreate.getBuildingPathingPixelMap();
			boolean buildLocationObstructed = false;
			final boolean canBeBuiltOnThem = unitTypeToCreate.isCanBeBuiltOnThem();
			if (canBeBuiltOnThem) {
				simulation.getWorldCollision().enumBuildingsAtPoint(this.target.getX(), this.target.getY(),
						this.buildOnBuildingIntersector.reset(this.target.getX(), this.target.getY()));
				buildLocationObstructed = (this.buildOnBuildingIntersector.getUnitToBuildOn() == null);
			}
			else if (buildingPathingPixelMap != null) {
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
				if (canBeBuiltOnThem) {
					CAbilityGoldMinable abilityGoldMine = null;
					if (this.buildOnBuildingIntersector.getUnitToBuildOn() != null) {
						for (final CAbility ability : this.buildOnBuildingIntersector.getUnitToBuildOn()
								.getAbilities()) {
							if ((ability instanceof CAbilityGoldMinable) && !ability.isDisabled()
									&& ((CAbilityGoldMinable) ability).isBaseMine()) {
								abilityGoldMine = (CAbilityGoldMinable) ability;
							}
						}
					}
					if (abilityGoldMine != null) {
						for (final CAbility ability : constructedStructure.getAbilities()) {
							if (ability instanceof CAbilityOverlayedMine) {
								final CAbilityOverlayedMine blightedGoldMine = (CAbilityOverlayedMine) ability;
								blightedGoldMine.setParentMine(this.buildOnBuildingIntersector.getUnitToBuildOn(),
										abilityGoldMine);
								this.buildOnBuildingIntersector.getUnitToBuildOn().setHidden(true);
								this.buildOnBuildingIntersector.getUnitToBuildOn().setPaused(true);
							}
						}
					}
				}
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
				this.unit.setPointAndCheckUnstuck(
						this.target.getX() + ((deltaX / delta) * unitTypeToCreate.getCollisionSize()),
						this.target.getY() + ((deltaY / delta) * unitTypeToCreate.getCollisionSize()), simulation);
				simulation.unitRepositioned(this.unit);
				simulation.getPlayer(playerIndex).addTechtreeInProgress(this.orderId);
				simulation.unitConstructedEvent(this.unit, constructedStructure);
				this.doneTick = simulation.getGameTurnTick() + delayAnimationTicks;
			}
			else {
				final CPlayer player = simulation.getPlayer(playerIndex);
				refund(player, unitTypeToCreate);
				simulation.getCommandErrorListener().showCantPlaceError(playerIndex);
				return this.unit.pollNextOrderBehavior(simulation);
			}
		}
		this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.WORK, 1.0f, true);
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
