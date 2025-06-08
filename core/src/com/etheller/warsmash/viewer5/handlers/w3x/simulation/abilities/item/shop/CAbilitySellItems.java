package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.shop;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItemType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitTypeRequirement;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.AbstractCAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory.CAbilityInventory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public final class CAbilitySellItems extends AbstractCAbility {
	private final Set<War3ID> itemsSold;
	private final Map<War3ID, ItemSellState> itemToSellState;
	private final boolean makeItems;

	public CAbilitySellItems(final int handleId, final List<War3ID> itemsSold, final boolean makeItems) {
		super(handleId, War3ID.fromString("Asei"));
		this.itemsSold = new HashSet<>(itemsSold);
		this.itemToSellState = new LinkedHashMap<>();
		this.makeItems = makeItems;
	}

	public Set<War3ID> getItemsSold() {
		return this.itemsSold;
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int playerIndex, final int orderId,
			final AbilityActivationReceiver receiver) {
		final War3ID itemTypeId = new War3ID(orderId);
		final ItemSellState itemSellState = this.itemToSellState.get(itemTypeId);
		if (itemSellState != null) {
			final CItemType itemType = game.getItemData().getItemType(itemTypeId);
			if (itemType != null) {
				final CPlayer requirementsPlayer = game.getPlayer(unit.getPlayerIndex());
				if (this.makeItems) {
					final List<CUnitTypeRequirement> requirements = itemType.getRequirements();
					boolean requirementsMet = true;
					for (final CUnitTypeRequirement requirement : requirements) {
						if (requirementsPlayer.getTechtreeUnlocked(requirement.getRequirement()) < requirement
								.getRequiredLevel()) {
							requirementsMet = false;
							receiver.missingRequirement(requirement.getRequirement(), requirement.getRequiredLevel());
						}
					}
					if (!requirementsMet) {
						return; // call made by missingRequirement above
					}
				}
				if (itemSellState.getStock() > 0) {
					final CPlayer player = game.getPlayer(playerIndex);
					if ((player.getGold() >= itemType.getGoldCost())) {
						if ((player.getLumber() >= itemType.getLumberCost())) {
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
					receiver.noChargesRemaining();
					final float fullCooldown = itemSellState.getCurrentIntervalTicks()
							* WarsmashConstants.SIMULATION_STEP_TIME;
					final float remainingCooldown = (itemSellState.getNextStockReplenishGameTurnTick()
							- game.getGameTurnTick()) * WarsmashConstants.SIMULATION_STEP_TIME;
					receiver.cooldownNotYetReady(remainingCooldown, fullCooldown);
				}
			}
			else {
				receiver.useOk();
			}
		}
		else {
			receiver.useOk();
		}
	}

	@Override
	public final void checkCanTarget(final CSimulation game, final CUnit unit, final int playerIndex, final int orderId,
			final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public final void checkCanTarget(final CSimulation game, final CUnit unit, final int playerIndex, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public final void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int playerIndex,
			final int orderId, final AbilityTargetCheckReceiver<Void> receiver) {
		final War3ID itemTypeId = new War3ID(orderId);
		if (this.itemsSold.contains(itemTypeId)) {
			receiver.targetOk(null);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int playerIndex,
			final int orderId, final AbilityTarget target) {
		return true;
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		for (final War3ID itemId : this.itemsSold) {
			final CItemType itemType = game.getItemData().getItemType(itemId);
			final int stockReplenishIntervalTicks = (int) (itemType.getStockReplenishInterval()
					/ WarsmashConstants.SIMULATION_STEP_TIME);
			final int stockStartDelayTicks = (int) (itemType.getStockStartDelay()
					/ WarsmashConstants.SIMULATION_STEP_TIME);
			final ItemSellState sellState = new ItemSellState(itemType.getStockMax(), stockReplenishIntervalTicks);
			sellState.init(game.getGameTurnTick(), stockStartDelayTicks);
			this.itemToSellState.put(itemId, sellState);
		}
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
		this.itemToSellState.clear();
	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
		if (!isDisabled()) {
			final CPlayer requirementsPlayer = game.getPlayer(unit.getPlayerIndex());
			final int gameTurnTick = game.getGameTurnTick();
			for (final Map.Entry<War3ID, ItemSellState> entry : this.itemToSellState.entrySet()) {
				final ItemSellState itemSellState = entry.getValue();
				final CItemType itemType = game.getItemData().getItemType(entry.getKey());
				if (itemType != null) {
					final List<CUnitTypeRequirement> requirements = itemType.getRequirements();
					boolean requirementsMet = true;
					for (final CUnitTypeRequirement requirement : requirements) {
						if (requirementsPlayer.getTechtreeUnlocked(requirement.getRequirement()) < requirement
								.getRequiredLevel()) {
							requirementsMet = false;
						}
					}
					if (requirementsMet) {
						itemSellState.update(gameTurnTick);
					}
				}
			}
		}
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int playerIndex, final int orderId,
			final CWidget target) {
		return null;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int playerIndex, final int orderId,
			final AbilityPointTarget point) {
		return null;
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int playerIndex,
			final int orderId) {
		final War3ID itemTypeId = new War3ID(orderId);
		final CAbilityNeutralBuilding neutralBuildingData = caster.getNeutralBuildingData();
		final ItemSellState itemSellState = this.itemToSellState.get(itemTypeId);
		if ((itemSellState != null) && (itemSellState.getStock() > 0)) {
			final CItemType itemType = game.getItemData().getItemType(itemTypeId);
			if ((neutralBuildingData != null) && (neutralBuildingData.getSelectedPlayerUnit(playerIndex) != null)) {
				final CUnit purchasingHero = neutralBuildingData.getSelectedPlayerUnit(playerIndex);
				final CAbilityInventory purchasingInventoryData = purchasingHero.getInventoryData();
				if ((purchasingInventoryData != null) && (itemType != null)) {
					if (game.getPlayer(playerIndex).charge(itemType.getGoldCost(), itemType.getLumberCost())) {
						itemSellState.chargeStock(1);
						final CItem newItem = game.createItem(itemTypeId, caster.getX(), caster.getY());
						purchasingInventoryData.giveItem(game, purchasingHero, newItem, false);
					}
				}
			}
			else {
				if (game.getPlayer(playerIndex).charge(itemType.getGoldCost(), itemType.getLumberCost())) {
					itemSellState.chargeStock(1);
					game.createItem(itemTypeId, caster.getX(), caster.getY());
				}
			}
		}
		return null;
	}

	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int playerIndex, final int orderId) {
	}

	@Override
	public void onSetUnitType(final CSimulation game, final CUnit cUnit) {
		// NOTE: this method not actually used, because CAbilityQueue is not Aliased
		onRemove(game, cUnit);
		final CUnitType unitType = cUnit.getUnitType();
		this.itemsSold.clear();
		this.itemsSold.addAll(unitType.getItemsSold());
		onAdd(game, cUnit);
	}

	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {
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

	public int getStock(final War3ID itemType) {
		final ItemSellState itemSellState = this.itemToSellState.get(itemType);
		if (itemSellState != null) {
			return itemSellState.getStock();
		}
		return 0;
	}
}
