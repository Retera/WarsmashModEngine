package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;

public abstract class AbstractCAbilityBuild extends AbstractCAbility implements CAbilityMenu {
	private static boolean REFUND_ON_ORDER_CANCEL = false;
	private final Set<War3ID> structuresBuilt;

	public AbstractCAbilityBuild(final int handleId, final List<War3ID> structuresBuilt) {
		super(handleId);
		this.structuresBuilt = new LinkedHashSet<>(structuresBuilt);
	}

	public Collection<War3ID> getStructuresBuilt() {
		return this.structuresBuilt;
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
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
								receiver.notEnoughResources(ResourceType.FOOD);
							}
						}
						else {
							receiver.notEnoughResources(ResourceType.LUMBER);
						}
					}
					else {
						receiver.notEnoughResources(ResourceType.GOLD);
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
	public final void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public final void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		if (this.structuresBuilt.contains(new War3ID(orderId))) {
			receiver.targetOk(target);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	public final void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityTarget target) {
		return true;
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
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
}
