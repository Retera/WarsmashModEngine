package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.shop;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItemType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
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
	private final List<War3ID> itemsSold;

	public CAbilitySellItems(final int handleId, final List<War3ID> itemsSold) {
		super(handleId, War3ID.fromString("Asei"));
		this.itemsSold = new ArrayList<>(itemsSold);
	}

	public List<War3ID> getItemsSold() {
		return this.itemsSold;
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		final int playerIndex = orderId & 0xFF; // TODO this is stupid, and should be passed as some "acting player" arg
		final int itemIndex = ((orderId & 0xFF00) >> 8) - 1;
		if ((itemIndex >= 0) && (itemIndex < this.itemsSold.size())) {
			final War3ID itemTypeId = this.itemsSold.get(itemIndex);
			final CItemType itemType = game.getItemData().getItemType(itemTypeId);
			if (itemType != null) {
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
				receiver.useOk();
			}
		}
		else {
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
		final int playerIndex = orderId & 0xFF; // TODO this is stupid, and should be passed as some "acting player" arg
		final int itemIndex = ((orderId & 0xFF00) >> 8) - 1;
		if ((itemIndex >= 0) && (itemIndex < this.itemsSold.size())) {
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
		final int playerIndex = orderId & 0xFF; // TODO this is stupid, and should be passed as some "acting player" arg
		final int itemIndex = ((orderId & 0xFF00) >> 8) - 1;
		final CAbilityNeutralBuilding neutralBuildingData = caster.getNeutralBuildingData();
		if ((itemIndex >= 0) && (itemIndex < this.itemsSold.size())) {
			final War3ID itemTypeId = this.itemsSold.get(itemIndex);
			final CItemType itemType = game.getItemData().getItemType(itemTypeId);
			if ((neutralBuildingData != null) && (neutralBuildingData.getSelectedPlayerUnit(playerIndex) != null)) {
				final CUnit purchasingHero = neutralBuildingData.getSelectedPlayerUnit(playerIndex);
				final CAbilityInventory purchasingInventoryData = purchasingHero.getInventoryData();
				if ((purchasingInventoryData != null) && (itemType != null)) {
					if (game.getPlayer(playerIndex).charge(itemType.getGoldCost(), itemType.getLumberCost())) {
						final CItem newItem = game.createItem(itemTypeId, caster.getX(), caster.getY());
						purchasingInventoryData.giveItem(game, purchasingHero, newItem, false);
					}
				}
			}
			else {
				if (game.getPlayer(playerIndex).charge(itemType.getGoldCost(), itemType.getLumberCost())) {
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
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {
	}

	@Override
	public void onSetUnitType(final CSimulation game, final CUnit cUnit) {
		// NOTE: this method not actually used, because CAbilityQueue is not Aliased
		final CUnitType unitType = cUnit.getUnitType();
		this.itemsSold.clear();
		this.itemsSold.addAll(unitType.getItemsSold());
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
}
