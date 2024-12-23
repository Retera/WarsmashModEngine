package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build;

import java.awt.image.BufferedImage;
import java.util.*;

import com.badlogic.gdx.math.Vector2;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitTypeRequirement;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.AbstractCAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.menu.CAbilityMenu;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CBuildingPathingType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.BuildOnBuildingIntersector;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public abstract class AbstractCAbilityBuild extends AbstractCAbility implements CAbilityMenu {
	private static boolean REFUND_ON_ORDER_CANCEL = false;
	private final Set<War3ID> structuresBuilt;

	public AbstractCAbilityBuild(final int handleId, final War3ID code, final List<War3ID> structuresBuilt) {
		super(handleId, code);
		this.structuresBuilt = new LinkedHashSet<>(structuresBuilt);
	}

	public Collection<War3ID> getStructuresBuilt() {
		return this.structuresBuilt;
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, int playerIndex,
			final int orderId, final AbilityActivationReceiver receiver) {
		final War3ID orderIdAsRawtype = new War3ID(orderId);
		if (this.structuresBuilt.contains(orderIdAsRawtype)) {
			final CUnitType unitType = game.getUnitData().getUnitType(orderIdAsRawtype);
			if (unitType != null) {
				final CPlayer player = game.getPlayer(unit.getPlayerIndex());
				final List<CUnitTypeRequirement> requirements = unitType.getRequirements();
				final boolean techtreeAllowedByMax = player.isTechtreeAllowedByMax(orderIdAsRawtype);
				boolean requirementsMet = techtreeAllowedByMax;
				for (final CUnitTypeRequirement requirement : requirements) {
					if (player.getTechtreeUnlocked(requirement.getRequirement()) < requirement.getRequiredLevel()) {
						requirementsMet = false;
					}
				}
				if (requirementsMet) {
					if ((player.getGold() >= unitType.getGoldCost())) {
						if ((player.getLumber() >= unitType.getLumberCost())) {
							if ((unitType.getFoodUsed() == 0)
									|| ((player.getFoodUsed() + unitType.getFoodUsed()) <= player.getFoodCap())) {

								receiver.useOk();
							}
							else {
								receiver.activationCheckFailed(CommandStringErrorKeys.NOT_ENOUGH_FOOD);
							}
						}
						else {
							receiver.activationCheckFailed(CommandStringErrorKeys.NOT_ENOUGH_LUMBER);
						}
					}
					else {
						receiver.activationCheckFailed(CommandStringErrorKeys.NOT_ENOUGH_GOLD);
					}
				}
				else {
					if (techtreeAllowedByMax) {
						for (final CUnitTypeRequirement requirement : requirements) {
							receiver.missingRequirement(requirement.getRequirement(), requirement.getRequiredLevel());
						}
					}
					else {
						receiver.techtreeMaximumReached();
					}
				}
			}
			else {
				receiver.useOk();
			}
		}
		else {
			/// ???
			receiver.useOk();
		}
	}

	@Override
	public final void checkCanTarget(final CSimulation game, final CUnit unit, int playerIndex, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public final void checkCanTarget(final CSimulation game, final CUnit unit, int playerIndex,
			final int orderId, final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		War3ID orderIdAsWar3ID = new War3ID(orderId);
		if (this.structuresBuilt.contains(orderIdAsWar3ID)) {
			final CUnitType unitTypeToCreate = game.getUnitData().getUnitType(orderIdAsWar3ID);
			final BufferedImage buildingPathingPixelMap = unitTypeToCreate.getBuildingPathingPixelMap();
			final boolean canBeBuiltOnThem = unitTypeToCreate.isCanBeBuiltOnThem();
			roundTargetPoint(target, unitTypeToCreate);
			float x = target.getX();
			float y = target.getY();
			boolean buildLocationObstructed = AbstractCAbilityBuild.isBuildLocationObstructed(game, unitTypeToCreate, buildingPathingPixelMap, canBeBuiltOnThem, x, y, unit, BuildOnBuildingIntersector.INSTANCE.reset(x, y));
			if(buildLocationObstructed) {
				receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_BUILD_THERE);
			} else {
				receiver.targetOk(target);
			}
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	public final void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, int playerIndex,
			final int orderId, final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, int playerIndex,
			final int orderId, final AbilityTarget target) {
		return true;
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, int playerIndex, final int orderId) {
		if (REFUND_ON_ORDER_CANCEL) {
			final CPlayer player = game.getPlayer(unit.getPlayerIndex());
			final War3ID orderIdAsRawtype = new War3ID(orderId);
			final CUnitType unitType = game.getUnitData().getUnitType(orderIdAsRawtype);
			player.refundFor(unitType);
			if (unitType.getFoodUsed() != 0) {
				player.setFoodUsed(player.getFoodUsed() - unitType.getFoodUsed());
			}
		}
	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
	}

	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {
	}

	public static boolean isBuildLocationObstructed(CSimulation simulation, CUnitType unitTypeToCreate, BufferedImage buildingPathingPixelMap, boolean canBeBuiltOnThem, float targetX, float targetY, CUnit worker, BuildOnBuildingIntersector buildOnBuildingIntersector) {
		boolean buildLocationObstructed = false;
		if (canBeBuiltOnThem) {
			simulation.getWorldCollision().enumBuildingsAtPoint(targetX, targetY,
					buildOnBuildingIntersector.reset(targetX, targetY));
			buildLocationObstructed = (buildOnBuildingIntersector.getUnitToBuildOn() == null);
		} else if (buildingPathingPixelMap != null) {
			final EnumSet<CBuildingPathingType> preventedPathingTypes = unitTypeToCreate.getPreventedPathingTypes();
			final EnumSet<CBuildingPathingType> requiredPathingTypes = unitTypeToCreate.getRequiredPathingTypes();

			if (!simulation.getPathingGrid().checkPathingTexture(targetX, targetY,
					(int) simulation.getGameplayConstants().getBuildingAngle(), buildingPathingPixelMap,
					preventedPathingTypes, requiredPathingTypes, simulation.getWorldCollision(), worker)) {
				buildLocationObstructed = true;
			}
		}
		return buildLocationObstructed;
	}

	public static void roundTargetPoint(Vector2 point, CUnitType unitType) {
		final BufferedImage buildingPathingPixelMap = unitType.getBuildingPathingPixelMap();
		if (buildingPathingPixelMap != null) {
			point.x = (float) Math.floor(point.x / 64f) * 64f;
			point.y = (float) Math.floor(point.y / 64f) * 64f;
			if (((buildingPathingPixelMap.getWidth() / 2) % 2) == 1) {
				point.x += 32f;
			}
			if (((buildingPathingPixelMap.getHeight() / 2) % 2) == 1) {
				point.y += 32f;
			}
		}
	}
}
