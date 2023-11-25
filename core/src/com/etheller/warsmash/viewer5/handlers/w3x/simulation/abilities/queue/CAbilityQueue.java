package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitTypeRequirement;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUpgradeType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUpgradeType.UpgradeLevel;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.AbstractCAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public final class CAbilityQueue extends AbstractCAbility {
	private final Set<War3ID> unitsTrained;
	private final Set<War3ID> researchesAvailable;

	public CAbilityQueue(final int handleId, final List<War3ID> unitsTrained, final List<War3ID> researchesAvailable) {
		super(handleId, War3ID.fromString("Aque"));
		this.unitsTrained = new LinkedHashSet<>(unitsTrained);
		this.researchesAvailable = new LinkedHashSet<>(researchesAvailable);
	}

	public Set<War3ID> getUnitsTrained() {
		return this.unitsTrained;
	}

	public Set<War3ID> getResearchesAvailable() {
		return this.researchesAvailable;
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		final War3ID orderIdAsRawtype = new War3ID(orderId);
		if (this.unitsTrained.contains(orderIdAsRawtype) || this.researchesAvailable.contains(orderIdAsRawtype)) {
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
				final boolean isHeroType = unitType.isHero();
				if (isHeroType) {
					final int heroCount = player.getHeroCount(game, true);
					final List<CUnitTypeRequirement> requirementsTier = unitType.getRequirementsTier(heroCount);
					for (final CUnitTypeRequirement requirement : requirementsTier) {
						if (player.getTechtreeUnlocked(requirement.getRequirement()) < requirement.getRequiredLevel()) {
							requirementsMet = false;
						}
					}
				}
				final boolean skipGoldLumberCost = isHeroType && (player.getHeroTokens() > 0);
				if (requirementsMet) {
					if ((player.getGold() >= unitType.getGoldCost()) || skipGoldLumberCost) {
						if ((player.getLumber() >= unitType.getLumberCost()) || skipGoldLumberCost) {
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
						if (isHeroType) {
							final int heroCount = player.getHeroCount(game, true);
							final List<CUnitTypeRequirement> requirementsTier = unitType.getRequirementsTier(heroCount);
							for (final CUnitTypeRequirement requirement : requirementsTier) {
								if (player.getTechtreeUnlocked(requirement.getRequirement()) < requirement
										.getRequiredLevel()) {
									receiver.missingRequirement(requirement.getRequirement(),
											requirement.getRequiredLevel());
								}
							}
						}
					}
					else {
						receiver.techtreeMaximumReached();
					}
				}
			}
			else {
				final CUpgradeType upgrade = game.getUpgradeData().getType(orderIdAsRawtype);
				if (upgrade != null) {
					final CPlayer player = game.getPlayer(unit.getPlayerIndex());
					final int inProgressCount = player.getTechtreeInProgress(orderIdAsRawtype);
					final int unlockedCount = player.getTechtreeUnlocked(orderIdAsRawtype);
					if (inProgressCount != 0) {
						receiver.techItemAlreadyInProgress();
					}
					else {
						final UpgradeLevel upgradeLevel = upgrade.getLevel(unlockedCount);
						if (upgradeLevel != null) {
							final List<CUnitTypeRequirement> requirements = upgradeLevel.getRequirements();
							final boolean techtreeAllowedByMax = player.isTechtreeAllowedByMax(orderIdAsRawtype);
							boolean requirementsMet = techtreeAllowedByMax;
							for (final CUnitTypeRequirement requirement : requirements) {
								if (player.getTechtreeUnlocked(requirement.getRequirement()) < requirement
										.getRequiredLevel()) {
									requirementsMet = false;
								}
							}
							if (requirementsMet) {
								if (player.getGold() >= upgrade.getGoldCost(unlockedCount)) {
									if (player.getLumber() >= upgrade.getLumberCost(unlockedCount)) {
										receiver.useOk();
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
										receiver.missingRequirement(requirement.getRequirement(),
												requirement.getRequiredLevel());
									}
								}
								else {
									receiver.techtreeMaximumReached();
								}
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
		if (this.unitsTrained.contains(new War3ID(orderId)) || this.researchesAvailable.contains(new War3ID(orderId))) {
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
			caster.cancelBuildQueueItem(game, 0);
		}
		else {
			final War3ID rawcode = new War3ID(orderId);
			if (this.unitsTrained.contains(rawcode)) {
				caster.queueTrainingUnit(game, rawcode);
			}
			else if (this.researchesAvailable.contains(rawcode)) {
				caster.queueResearch(game, rawcode);
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

	@Override
	public void onSetUnitType(final CSimulation game, final CUnit cUnit) {
		// NOTE: this method not actually used, because CAbilityQueue is not Aliased
		final CUnitType unitType = cUnit.getUnitType();
		this.unitsTrained.clear();
		this.researchesAvailable.clear();
		this.unitsTrained.addAll(unitType.getUnitsTrained());
		this.researchesAvailable.addAll(unitType.getResearchesAvailable());
	}

	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {
		// refund stuff when building dies
		while (cUnit.getBuildQueueTypes()[0] != null) {
			cUnit.cancelBuildQueueItem(game, 0);
		}
	}

	@Override
	public boolean isPhysical() {
		return false;
	}

	@Override
	public boolean isUniversal() {
		return false;
	}

	@Override
	public CAbilityCategory getAbilityCategory() {
		return CAbilityCategory.CORE;
	}
}
