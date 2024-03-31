package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItemType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericNoIconAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.SingleOrderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.item.shop.CAbilityShopPurhaseItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.inventory.CBehaviorDropItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.inventory.CBehaviorGetItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.inventory.CBehaviorGiveItemToHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrderNoTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrderTargetPoint;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrderTargetWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

public class CAbilityInventory extends AbstractGenericNoIconAbility {
	private final boolean canDropItems;
	private final boolean canGetItems;
	private final boolean canUseItems;
	private final boolean dropItemsOnDeath;
	private final CItem[] itemsHeld;
	private final List<CAbility>[] itemsHeldAbilities;
	private CBehaviorGetItem behaviorGetItem;
	private CBehaviorDropItem behaviorDropItem;
	private CBehaviorGiveItemToHero behaviorGiveItem;

	public CAbilityInventory(final int handleId, final War3ID code, final War3ID alias, final boolean canDropItems,
			final boolean canGetItems, final boolean canUseItems, final boolean dropItemsOnDeath,
			final int itemCapacity) {
		super(handleId, code, alias);
		this.canDropItems = canDropItems;
		this.canGetItems = canGetItems;
		this.canUseItems = canUseItems;
		this.dropItemsOnDeath = dropItemsOnDeath;
		this.itemsHeld = new CItem[itemCapacity];
		this.itemsHeldAbilities = new List[itemCapacity];
		for (int i = 0; i < this.itemsHeldAbilities.length; i++) {
			this.itemsHeldAbilities[i] = new ArrayList<>();
		}
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.behaviorGetItem = new CBehaviorGetItem(unit, this);
		this.behaviorDropItem = new CBehaviorDropItem(unit, this);
		this.behaviorGiveItem = new CBehaviorGiveItemToHero(unit, this);
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
		for (int i = 0; i < this.itemsHeld.length; i++) {
			if (this.itemsHeld[i] != null) {
				dropItem(game, unit, i, unit.getX(), unit.getY(), false);
			}
		}
	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {

	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityTarget target) {
		if ((orderId >= OrderIds.itemdrag00) && (orderId <= OrderIds.itemdrag05)) {
			for (int i = 0; i < this.itemsHeld.length; i++) {
				if (this.itemsHeld[i] == target) {
					final CItem temp = this.itemsHeld[i];
					final List<CAbility> swapList = this.itemsHeldAbilities[i];
					final int dragDropDestinationIndex = orderId - OrderIds.itemdrag00;
					this.itemsHeld[i] = this.itemsHeld[dragDropDestinationIndex];
					this.itemsHeldAbilities[i] = this.itemsHeldAbilities[dragDropDestinationIndex];
					this.itemsHeld[dragDropDestinationIndex] = temp;
					this.itemsHeldAbilities[dragDropDestinationIndex] = swapList;
					return false;
				}
			}
		}
		else if ((orderId >= OrderIds.itemuse00) && (orderId <= OrderIds.itemuse05)) {
			final int slot = orderId - OrderIds.itemuse00;
			final List<CAbility> itemsHeldAbilitiesForSlot = this.itemsHeldAbilities[slot];
			if (!itemsHeldAbilitiesForSlot.isEmpty()) {
				final CAbility cAbility = itemsHeldAbilitiesForSlot.get(0);
				int forwardedOrderId = orderId;
				if (cAbility instanceof SingleOrderAbility) {
					forwardedOrderId = ((SingleOrderAbility) cAbility).getBaseOrderId();
				}
				final boolean checkResult = cAbility.checkBeforeQueue(game, caster, forwardedOrderId, target);
				if (!checkResult) {
					// we will never call begin, so we need to consume a charge of perishables here
					// assuming this is a no-queue instant use perishable... later if we have some
					// other weird case where "check before queue" false is supposed to mean you
					// can't use the skill, then this would consume charges without using it, and
					// that would be stupid but I don't think we will do that since checkCanUse
					// should be failing at that point. So then we should have never called
					// checkBeforeQueue.
					final CItem cItem = this.itemsHeld[slot];
					consumePerishableCharge(game, caster, slot, cItem);
				}
				return checkResult;
			}
		}
		return super.checkBeforeQueue(game, caster, orderId, target);
	}

	private void consumePerishableCharge(final CSimulation game, final CUnit caster, final int slot,
			final CItem cItem) {
		final int updatedCharges = cItem.getCharges() - 1;
		if (updatedCharges >= 0) {
			cItem.setCharges(updatedCharges);
			if (updatedCharges == 0) {
				if (cItem.getItemType().isPerishable()) {
					dropItem(game, caster, slot, caster.getX(), caster.getY(), false);
					game.removeItem(cItem);
				}
			}
		}
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int orderId) {

	}

	public int getItemCapacity() {
		return this.itemsHeld.length;
	}

	public CItem getItemInSlot(final int slotIndex) {
		if ((slotIndex < 0) || (slotIndex >= this.itemsHeld.length)) {
			return null;
		}
		return this.itemsHeld[slotIndex];
	}

	public boolean isDropItemsOnDeath() {
		return this.dropItemsOnDeath;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId, final CWidget target) {
		if ((orderId >= OrderIds.itemuse00) && (orderId <= OrderIds.itemuse05)) {
			final int slot = orderId - OrderIds.itemuse00;
			final List<CAbility> itemsHeldAbilitiesForSlot = this.itemsHeldAbilities[slot];
			if (!itemsHeldAbilitiesForSlot.isEmpty()) {
				final CAbility ability = itemsHeldAbilitiesForSlot.get(0);
				int forwardedOrderId = orderId;
				if (ability instanceof SingleOrderAbility) {
					forwardedOrderId = ((SingleOrderAbility) ability).getBaseOrderId();
				}
				final CBehavior behavior = ability.begin(game, caster, forwardedOrderId, target);
				final CItem cItem = this.itemsHeld[slot];
				consumePerishableCharge(game, caster, slot, cItem);
				return behavior;
			}
		}
		final CItem targetItem = target.visit(AbilityTargetVisitor.ITEM);
		if (targetItem != null) {
			return this.behaviorGetItem.reset(game, (CItem) target);
		}
		return caster.pollNextOrderBehavior(game);
	}

	public CBehavior beginDropItem(final CSimulation game, final CUnit caster, final int orderId,
			final CItem itemToDrop, final AbilityPointTarget target) {
		return this.behaviorDropItem.reset(game, itemToDrop, target);
	}

	public CBehavior beginDropItem(final CSimulation game, final CUnit caster, final int orderId,
			final CItem itemToDrop, final CUnit targetHero) {
		return this.behaviorGiveItem.reset(game, itemToDrop, targetHero);
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		if ((orderId >= OrderIds.itemuse00) && (orderId <= OrderIds.itemuse05)) {
			final int slot = orderId - OrderIds.itemuse00;
			final List<CAbility> itemsHeldAbilitiesForSlot = this.itemsHeldAbilities[slot];
			if (!itemsHeldAbilitiesForSlot.isEmpty()) {
				final CAbility ability = itemsHeldAbilitiesForSlot.get(0);
				int forwardedOrderId = orderId;
				if (ability instanceof SingleOrderAbility) {
					forwardedOrderId = ((SingleOrderAbility) ability).getBaseOrderId();
				}
				final CBehavior behavior = ability.begin(game, caster, forwardedOrderId, point);
				final CItem cItem = this.itemsHeld[slot];
				consumePerishableCharge(game, caster, slot, cItem);
				return behavior;
			}
		}
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		if ((orderId >= OrderIds.itemuse00) && (orderId <= OrderIds.itemuse05)) {
			final int slot = orderId - OrderIds.itemuse00;
			final List<CAbility> itemsHeldAbilitiesForSlot = this.itemsHeldAbilities[slot];
			if (!itemsHeldAbilitiesForSlot.isEmpty()) {
				final CAbility ability = itemsHeldAbilitiesForSlot.get(0);
				int forwardedOrderId = orderId;
				if (ability instanceof SingleOrderAbility) {
					forwardedOrderId = ((SingleOrderAbility) ability).getBaseOrderId();
				}
				final CBehavior behavior = ability.beginNoTarget(game, caster, forwardedOrderId);
				final CItem cItem = this.itemsHeld[slot];
				consumePerishableCharge(game, caster, slot, cItem);
				return behavior;
			}
		}
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (((orderId == OrderIds.getitem) || (orderId == OrderIds.smart)) && !target.isDead()) {
			if (target instanceof CItem) {
				if (this.canGetItems) {
					final CItem targetItem = (CItem) target;
					if (!targetItem.isHidden()) {
						receiver.targetOk(target);
					}
					else {
						receiver.orderIdNotAccepted();
					}
				} else {
					receiver.targetCheckFailed(CommandStringErrorKeys.UNABLE_TO_PICK_UP_THIS_ITEM);
				}
			}
			else {
				receiver.orderIdNotAccepted();
			}
		}
		else {
			if ((orderId >= OrderIds.itemdrag00) && (orderId <= OrderIds.itemdrag05)) {
				if (target instanceof CItem) {
					final int slot = getSlot((CItem) target);
					if (slot != -1) {
						receiver.targetOk(target);
					}
					else {
						receiver.orderIdNotAccepted();
					}
				}
				else {
					receiver.orderIdNotAccepted();
				}
			}
			else if (orderId == OrderIds.dropitem) {
				if (target instanceof CUnit) {
					final CUnit hero = (CUnit) target;
					if (game.getPlayer(hero.getPlayerIndex()).hasAlliance(unit.getPlayerIndex(), CAllianceType.PASSIVE)
							&& (hero != unit)) {
						if (hero.getInventoryData() != null) {
							receiver.targetOk(target);
						}
						else if (hero.getFirstAbilityOfType(CAbilityShopPurhaseItem.class) != null) {
							receiver.targetOk(target);
						}
						else {
							receiver.orderIdNotAccepted();
						}
					}
					else {
						receiver.orderIdNotAccepted();
					}
				}
				else {
					receiver.orderIdNotAccepted();
				}
			}
			else {
				if ((orderId >= OrderIds.itemuse00) && (orderId <= OrderIds.itemuse05)) {
					final int slot = orderId - OrderIds.itemuse00;
					final List<CAbility> itemsHeldAbilitiesForSlot = this.itemsHeldAbilities[slot];
					if (!itemsHeldAbilitiesForSlot.isEmpty()) {
						final CAbility ability = itemsHeldAbilitiesForSlot.get(0);
						int forwardedOrderId = orderId;
						if (ability instanceof SingleOrderAbility) {
							forwardedOrderId = ((SingleOrderAbility) ability).getBaseOrderId();
						}
						ability.checkCanTarget(game, unit, forwardedOrderId, target, receiver);
					}
					else {
						receiver.orderIdNotAccepted();
					}
				}
				else {
					receiver.orderIdNotAccepted();
				}
			}
		}
	}

	public int getSlot(final CItem target) {
		int slot = -1;
		for (int i = 0; i < this.itemsHeld.length; i++) {
			if (this.itemsHeld[i] == target) {
				slot = i;
			}
		}
		return slot;
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityPointTarget target, final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		if (orderId != OrderIds.dropitem) {
			if ((orderId >= OrderIds.itemuse00) && (orderId <= OrderIds.itemuse05)) {
				final int slot = orderId - OrderIds.itemuse00;
				final List<CAbility> itemsHeldAbilitiesForSlot = this.itemsHeldAbilities[slot];
				if (!itemsHeldAbilitiesForSlot.isEmpty()) {
					final CAbility ability = itemsHeldAbilitiesForSlot.get(0);
					int forwardedOrderId = orderId;
					if (ability instanceof SingleOrderAbility) {
						forwardedOrderId = ((SingleOrderAbility) ability).getBaseOrderId();
					}
					ability.checkCanTarget(game, unit, forwardedOrderId, target, receiver);
				}
				else {
					receiver.orderIdNotAccepted();
				}
			}
			else {
				receiver.orderIdNotAccepted();
			}
		}
		else {
			receiver.targetOk(target);
		}
	}

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		if ((orderId >= OrderIds.itemuse00) && (orderId <= OrderIds.itemuse05)) {
			final int slot = orderId - OrderIds.itemuse00;
			final List<CAbility> itemsHeldAbilitiesForSlot = this.itemsHeldAbilities[slot];
			if (!itemsHeldAbilitiesForSlot.isEmpty()) {
				final CAbility ability = itemsHeldAbilitiesForSlot.get(0);
				int forwardedOrderId = orderId;
				if (ability instanceof SingleOrderAbility) {
					forwardedOrderId = ((SingleOrderAbility) ability).getBaseOrderId();
				}
				ability.checkCanTargetNoTarget(game, unit, forwardedOrderId, receiver);
			}
			else {
				receiver.orderIdNotAccepted();
			}
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		if ((orderId >= OrderIds.itemuse00) && (orderId <= OrderIds.itemuse05)) {
			if (this.canUseItems) {
				final int slot = orderId - OrderIds.itemuse00;
				if (this.itemsHeldAbilities[slot].size() < 1) {
					receiver.notAnActiveAbility();
				}
				else {
					final List<CAbility> itemsHeldAbilitiesForSlot = this.itemsHeldAbilities[slot];
					if (!itemsHeldAbilitiesForSlot.isEmpty()) {
						final CAbility ability = itemsHeldAbilitiesForSlot.get(0);
						int forwardedOrderId = orderId;
						if (ability instanceof SingleOrderAbility) {
							forwardedOrderId = ((SingleOrderAbility) ability).getBaseOrderId();
						}
						ability.checkCanUse(game, unit, forwardedOrderId, receiver);
					}
					else {
						receiver.notAnActiveAbility();
					}
				}
			}
			else {
				receiver.activationCheckFailed(CommandStringErrorKeys.UNABLE_TO_USE_THIS_ITEM);
			}
		}
		else if(orderId == OrderIds.dropitem && !this.canDropItems) {
			receiver.activationCheckFailed(CommandStringErrorKeys.UNABLE_TO_DROP_THIS_ITEM);
		} else {
			receiver.useOk();
		}
	}

	public int giveItem(final CSimulation simulation, final CUnit hero, final CItem item,
			final boolean playUserUISounds) {
		return giveItem(simulation, hero, item, 0, playUserUISounds);
	}

	/**
	 * Attempts to give the hero the specified item, returning the item slot to
	 * which the item is added or -1 if no available slot is found
	 *
	 * @param item
	 * @return
	 */
	public int giveItem(final CSimulation simulation, final CUnit hero, final CItem item, final int slotPreference,
			final boolean playUserUISounds) {
		if ((item != null) && !item.isDead() && !item.isHidden()) {
			final CItemType itemType = item.getItemType();
			if (this.canUseItems && itemType.isUseAutomaticallyWhenAcquired()) {
				if (itemType.isActivelyUsed()) {
					item.setLife(simulation, 0);
					// TODO when we give unit ability here, then use ability
					final List<CAbility> addedAbilities = new ArrayList<>();
					for (final War3ID abilityId : item.getItemType().getAbilityList()) {
						final CAbilityType<?> abilityType = simulation.getAbilityData().getAbilityType(abilityId);
						if (abilityType != null) {
							final CAbility abilityFromItem = abilityType
									.createAbility(simulation.getHandleIdAllocator().createId());
							abilityFromItem.setIconShowing(false);
							abilityFromItem.setItemAbility(item, -1);
							hero.add(simulation, abilityFromItem);
							if (abilityFromItem instanceof SingleOrderAbility) {
								final int baseOrderId = ((SingleOrderAbility) abilityFromItem).getBaseOrderId();

								final BooleanAbilityTargetCheckReceiver<CWidget> booleanUnitTargetReceiver = BooleanAbilityTargetCheckReceiver
										.<CWidget>getInstance().reset();
								abilityFromItem.checkCanTarget(simulation, hero, baseOrderId, hero, booleanUnitTargetReceiver);
								if (booleanUnitTargetReceiver.isTargetable()) {
									hero.order(simulation,
											new COrderTargetWidget(abilityFromItem.getHandleId(), baseOrderId, hero.getHandleId(), false), false);
									
								} else {
									final BooleanAbilityTargetCheckReceiver<AbilityPointTarget> booleanTargetReceiver = BooleanAbilityTargetCheckReceiver
											.<AbilityPointTarget>getInstance().reset();
									AbilityPointTarget tar = new AbilityPointTarget(hero.getX(), hero.getY());
									abilityFromItem.checkCanTarget(simulation, hero, baseOrderId, tar, booleanTargetReceiver);
									
									if (booleanTargetReceiver.isTargetable()) {hero.order(simulation,
										new COrderTargetPoint(abilityFromItem.getHandleId(), baseOrderId, tar, false), false);
									} else {
										hero.order(simulation,
												new COrderNoTarget(abilityFromItem.getHandleId(), baseOrderId, false), false);
									}
								}
							}
							addedAbilities.add(abilityFromItem);
						}
					}
					hero.onPickUpItem(simulation, item, true);
					for (final CAbility ability : addedAbilities) {
						hero.remove(simulation, ability);
					}
				}
			}
			else {
				for (int i = 0; i < this.itemsHeld.length; i++) {
					final int itemIndex = (i + slotPreference) % this.itemsHeld.length;
					if (this.itemsHeld[itemIndex] == null) {
						this.itemsHeld[itemIndex] = item;
						item.setHidden(true);
						item.setContainedInventory(this, hero);
						if (this.canUseItems) {
							for (final War3ID abilityId : item.getItemType().getAbilityList()) {
								final CAbilityType<?> abilityType = simulation.getAbilityData()
										.getAbilityType(abilityId);
								if (abilityType != null) {
									final CAbility abilityFromItem = abilityType
											.createAbility(simulation.getHandleIdAllocator().createId());
									abilityFromItem.setIconShowing(false);
									abilityFromItem.setItemAbility(item, itemIndex);
									hero.add(simulation, abilityFromItem);
									this.itemsHeldAbilities[itemIndex].add(abilityFromItem);
								}
							}
						}
						hero.onPickUpItem(simulation, item, true);
						return itemIndex;
					}
				}
				if (playUserUISounds) {
					simulation.getCommandErrorListener().showInterfaceError(hero.getPlayerIndex(), CommandStringErrorKeys.INVENTORY_IS_FULL);
				}
			}
		}
		return -1;
	}

	public void dropItem(final CSimulation simulation, final CUnit hero, final int slotIndex, final float x,
			final float y, final boolean playUserUISounds) {
		final CItem droppedItem = this.itemsHeld[slotIndex];
		hero.onDropItem(simulation, droppedItem, playUserUISounds);
		this.itemsHeld[slotIndex] = null;
		for (final CAbility ability : this.itemsHeldAbilities[slotIndex]) {
			hero.remove(simulation, ability);
		}
		this.itemsHeldAbilities[slotIndex].clear();
		droppedItem.setHidden(false);
		droppedItem.setContainedInventory(null, null);
		droppedItem.setPointAndCheckUnstuck(x, y, simulation);
	}

	public void dropItem(final CSimulation simulation, final CUnit hero, final CItem itemToDrop, final float x,
			final float y, final boolean playUserUISounds) {
		boolean foundItem = false;
		int index = -1;
		for (int i = 0; i < this.itemsHeld.length; i++) {
			if (this.itemsHeld[i] == itemToDrop) {
				this.itemsHeld[i] = null;
				index = i;
				foundItem = true;
			}
		}
		if (foundItem) {
			hero.onDropItem(simulation, itemToDrop, playUserUISounds);
			itemToDrop.setHidden(false);
			itemToDrop.setContainedInventory(null, null);
			for (final CAbility ability : this.itemsHeldAbilities[index]) {
				hero.remove(simulation, ability);
			}
			this.itemsHeldAbilities[index].clear();
			itemToDrop.setPointAndCheckUnstuck(x, y, simulation);
		}
	}

	@Override
	public void onDeath(final CSimulation game, final CUnit hero) {
		if (this.dropItemsOnDeath) {
			for (int i = 0; i < this.itemsHeld.length; i++) {
				if (this.itemsHeld[i] != null) {
					dropItem(game, hero, i, hero.getX(), hero.getY(), false);
				}
			}
		}
	}

}
