package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItemType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericNoIconAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.inventory.CBehaviorDropItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.inventory.CBehaviorGetItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;

public class CAbilityInventory extends AbstractGenericNoIconAbility {
	private final boolean canDropItems;
	private final boolean canGetItems;
	private final boolean canUseItems;
	private final boolean dropItemsOnDeath;
	private final CItem[] itemsHeld;
	private CBehaviorGetItem behaviorGetItem;
	private CBehaviorDropItem behaviorDropItem;

	public CAbilityInventory(final int handleId, final War3ID alias, final boolean canDropItems,
			final boolean canGetItems, final boolean canUseItems, final boolean dropItemsOnDeath,
			final int itemCapacity) {
		super(handleId, alias);
		this.canDropItems = canDropItems;
		this.canGetItems = canGetItems;
		this.canUseItems = canUseItems;
		this.dropItemsOnDeath = dropItemsOnDeath;
		this.itemsHeld = new CItem[itemCapacity];
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.behaviorGetItem = new CBehaviorGetItem(unit, this);
		this.behaviorDropItem = new CBehaviorDropItem(unit, this);
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
					final int dragDropDestinationIndex = orderId - OrderIds.itemdrag00;
					this.itemsHeld[i] = this.itemsHeld[dragDropDestinationIndex];
					this.itemsHeld[dragDropDestinationIndex] = temp;
					return false;
				}
			}
		}
		return super.checkBeforeQueue(game, caster, orderId, target);
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

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int orderId,
			final AbilityPointTarget point) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int orderId) {
		// TODO Auto-generated method stub
		return null;
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
			receiver.orderIdNotAccepted();
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
		if (orderId == OrderIds.dropitem) {
			receiver.orderIdNotAccepted();
		}
	}

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int orderId,
			final AbilityActivationReceiver receiver) {
		receiver.useOk();
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
			if (itemType.isUseAutomaticallyWhenAcquired()) {
				if (itemType.isActivelyUsed()) {
					item.setLife(simulation, 0);
					// TODO when we give unit ability here, then use ability
				}
			}
			else {
				for (int i = 0; i < this.itemsHeld.length; i++) {
					if (this.itemsHeld[i] == null) {
						this.itemsHeld[i] = item;
						item.setHidden(true);
						hero.onPickUpItem(simulation, item, true);
						return i;
					}
				}
				if (playUserUISounds) {
					simulation.getCommandErrorListener(hero.getPlayerIndex()).showInventoryFullError();
				}
			}
		}
		return -1;
	}

	public void dropItem(final CSimulation simulation, final CUnit hero, final int slotIndex, final float x,
			final float y, final boolean playUserUISounds) {
		final CItem droppedItem = this.itemsHeld[slotIndex];
		hero.onDropItem(simulation, droppedItem, true);
		this.itemsHeld[slotIndex] = null;
		droppedItem.setHidden(false);
		droppedItem.setPointAndCheckUnstuck(x, y, simulation);
	}

	public void dropItem(final CSimulation simulation, final CUnit hero, final CItem itemToDrop, final float x,
			final float y, final boolean playUserUISounds) {
		boolean foundItem = false;
		for (int i = 0; i < this.itemsHeld.length; i++) {
			if (this.itemsHeld[i] == itemToDrop) {
				this.itemsHeld[i] = null;
				foundItem = true;
			}
		}
		if (foundItem) {
			hero.onDropItem(simulation, itemToDrop, true);
			itemToDrop.setHidden(false);
			itemToDrop.setPointAndCheckUnstuck(x, y, simulation);
		}
	}

}
