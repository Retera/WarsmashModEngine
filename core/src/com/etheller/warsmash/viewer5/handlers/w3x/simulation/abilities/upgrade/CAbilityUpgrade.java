package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.upgrade;

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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;

public final class CAbilityUpgrade extends AbstractCAbility {
	private final Set<War3ID> upgradesTo;

	public CAbilityUpgrade(final int handleId, final List<War3ID> upgradesTo) {
		super(handleId);
		this.upgradesTo = new LinkedHashSet<>(upgradesTo);
	}

	public Set<War3ID> getUpgradesTo() {
		return this.upgradesTo;
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		if (unit.getBuildQueueTypes()[0] != null) {
			receiver.disabled();
			return;
		}
		final War3ID orderIdAsRawtype = new War3ID(orderId);
		if (this.upgradesTo.contains(orderIdAsRawtype) && (unit.getBuildQueue()[0] == null)) {
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
					int relativeOffsetGold;
					int relativeOffsetLumber;
					final CUnitType existingUnitType = unit.getUnitType();
					if (game.getGameplayConstants().isRelativeUpgradeCosts()) {
						relativeOffsetGold = existingUnitType.getGoldCost();
						relativeOffsetLumber = existingUnitType.getLumberCost();
					}
					else {
						relativeOffsetGold = 0;
						relativeOffsetLumber = 0;
					}
					if ((player.getGold() + relativeOffsetGold) >= unitType.getGoldCost()) {
						if ((player.getLumber() + relativeOffsetLumber) >= unitType.getLumberCost()) {
							final int foodNeeded = unitType.getFoodUsed() - existingUnitType.getFoodUsed();
							if ((foodNeeded == 0) || ((player.getFoodUsed() + foodNeeded) <= player.getFoodCap())) {
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
		receiver.orderIdNotAccepted();
	}

	@Override
	public final void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		if (this.upgradesTo.contains(new War3ID(orderId))) {
			receiver.targetOk(null);
		}
		else if (orderId == OrderIds.cancel) {
			receiver.targetOk(null);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityTarget target) {
		return true;
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {

	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {

	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		return null;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		return null;
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		if (orderId == OrderIds.cancel) {
			caster.cancelUpgrade(game);
		}
		else {
			final War3ID rawcode = new War3ID(orderId);
			if (this.upgradesTo.contains(rawcode)) {
				caster.beginUpgrade(game, rawcode);
			}
		}
		return null;
	}

	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	public void onSetUnitType(final CUnitType unitType) {
		this.upgradesTo.clear();
		this.upgradesTo.addAll(unitType.getUpgradesTo());
	}

	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {
	}
}
