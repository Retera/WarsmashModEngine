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
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.inventory.CBehaviorDropItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.inventory.CBehaviorGetItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.inventory.CBehaviorGiveItemToHero;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

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

	public CAbilityInventory(final int handleId, final War3ID alias, final boolean canDropItems,
			final boolean canGetItems, final boolean canUseItems, final boolean dropItemsOnDeath,
			final int itemCapacity) {
		super(handleId, alias);
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
			final CAbility cAbility = this.itemsHeldAbilities[slot].get(0);
			int forwardedOrderId = orderId;
			if (cAbility instanceof GenericSingleIconActiveAbility) {
				forwardedOrderId = ((GenericSingleIconActiveAbility) cAbility).getBaseOrderId();
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
		return super.checkBeforeQueue(game, caster, orderId, target);
	}

	private void consumePerishableCharge(final CSimulation game, final CUnit caster, final int slot,
			final CItem cItem) {
		if (cItem.getItemType().isPerishable()) {
			dropItem(game, caster, slot, caster.getX(), caster.getY(), false);
			game.removeItem(cItem);
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
		return this.behaviorGetItem.reset((CItem) target);
	}

	public CBehavior beginDropItem(final CSimulation game, final CUnit caster, final int orderId,
			final CItem itemToDrop, final AbilityPointTarget target) {
		return this.behaviorDropItem.reset(itemToDrop, target);
	}

	public CBehavior beginDropItem(final CSimulation game, final CUnit caster, final int orderId,
			final CItem itemToDrop, final CUnit targetHero) {
		return this.behaviorGiveItem.reset(itemToDrop, targetHero);
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		final int slot = orderId - OrderIds.itemuse00;
		final CBehavior behavior = this.itemsHeldAbilities[slot].get(0).beginNoTarget(game, caster, orderId);
		final CItem cItem = this.itemsHeld[slot];
		consumePerishableCharge(game, caster, slot, cItem);
		return behavior;
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int orderId, final CWidget target,
			final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (((orderId == OrderIds.getitem) || (orderId == OrderIds.smart)) && !target.isDead()) {
			if (target instanceof CItem) {
				final CItem targetItem = (CItem) target;
				if (!targetItem.isHidden()) {
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
					if ((hero.getInventoryData() != null) && game.getPlayer(hero.getPlayerIndex())
							.hasAlliance(unit.getPlayerIndex(), CAllianceType.PASSIVE) && (hero != unit)) {
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
			receiver.orderIdNotAccepted();
		}
		else {
			receiver.targetOk(target);
		}
	}

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		if ((orderId >= OrderIds.itemuse00) && (orderId <= OrderIds.itemuse05)) {
			receiver.targetOk(null);
		}
		else {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		if (this.canUseItems && (orderId >= OrderIds.itemuse00) && (orderId <= OrderIds.itemuse05)) {
			final int slot = orderId - OrderIds.itemuse00;
			if (this.itemsHeldAbilities[slot].size() < 1) {
				receiver.notAnActiveAbility();
			}
			else {
				this.itemsHeldAbilities[slot].get(0).checkCanUse(game, unit, orderId, receiver);
			}
		}
		else {
			receiver.useOk();
		}
	}

	/**
	 * Attempts to give the hero the specified item, returning the item slot to
	 * which the item is added or -1 if no available slot is found
	 *
	 * @param item
	 * @return
	 */
	public int giveItem(final CSimulation simulation, final CUnit hero, final CItem item,
			final boolean playUserUISounds) {
		if ((item != null) && !item.isDead() && !item.isHidden()) {
			final CItemType itemType = item.getItemType();
			if (this.canUseItems && itemType.isUseAutomaticallyWhenAcquired()) {
				if (itemType.isActivelyUsed()) {
					item.setLife(simulation, 0);
					// TODO when we give unit ability here, then use ability
					for (final War3ID abilityId : item.getItemType().getAbilityList()) {
						final CAbilityType<?> abilityType = simulation.getAbilityData().getAbilityType(abilityId);
						if (abilityType != null) {
							final CAbility abilityFromItem = abilityType
									.createAbility(simulation.getHandleIdAllocator().createId());
							abilityFromItem.setIconShowing(false);
							hero.add(simulation, abilityFromItem);
						}
					}
					hero.onPickUpItem(simulation, item, true);
				}
			}
			else {
				for (int i = 0; i < this.itemsHeld.length; i++) {
					if (this.itemsHeld[i] == null) {
						this.itemsHeld[i] = item;
						item.setHidden(true);
						if (this.canUseItems) {
							for (final War3ID abilityId : item.getItemType().getAbilityList()) {
								final CAbilityType<?> abilityType = simulation.getAbilityData()
										.getAbilityType(abilityId);
								if (abilityType != null) {
									final CAbility abilityFromItem = abilityType
											.createAbility(simulation.getHandleIdAllocator().createId());
									abilityFromItem.setIconShowing(false);
									hero.add(simulation, abilityFromItem);
									this.itemsHeldAbilities[i].add(abilityFromItem);
								}
							}
						}
						hero.onPickUpItem(simulation, item, true);
						return i;
					}
				}
				if (playUserUISounds) {
					simulation.getCommandErrorListener().showInventoryFullError(hero.getPlayerIndex());
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
