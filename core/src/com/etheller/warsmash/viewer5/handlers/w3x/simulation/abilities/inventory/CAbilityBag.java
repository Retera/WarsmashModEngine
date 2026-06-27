package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericAliasedAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.inventory.CBehaviorBagGetItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CommandStringErrorKeys;

/**
 * A "bag" of item storage. This is the bridge between the Warcraft III item
 * concept and the World of Warcraft bag/container UI: a bag is provided to a
 * unit as the ability granted by a carried Warcraft III item (rawcode "Abag",
 * whose DataA1 sets the slot count), so a hero that carries several bag items
 * has several {@link CAbilityBag} instances. The built-in WoW backpack is one of
 * these too, owned directly by the player pawn rather than by a carried item.
 *
 * <p>
 * The contents are held here as the bag's own state store. The ability is
 * otherwise passive and intentionally invisible to the Warcraft III command
 * card and other ability visitors (see {@link #visit}); all interaction happens
 * through the WoW container natives that read/write this state.
 */
public class CAbilityBag extends AbstractGenericAliasedAbility implements CItemSlotHolder {

	private final CItem[] contents;
	private CItem item;
	private CBehaviorBagGetItem behaviorGetItem;

	public CAbilityBag(final int handleId, final War3ID code, final War3ID alias, final int slotCount) {
		super(handleId, code, alias);
		this.contents = new CItem[Math.max(0, slotCount)];
	}

	/**
	 * Stores the item in the first free bag slot. Unlike {@link CAbilityInventory},
	 * a bag does not grant the item's abilities to the carrier (items sit dormant in
	 * the bag until moved into the inventory), so this only files the item away and
	 * fires the pickup event. Returns the slot used or -1 when the bag is full.
	 */
	public int giveItem(final CSimulation simulation, final CUnit hero, final CItem item,
			final boolean playUserUISounds) {
		if ((item == null) || item.isDead() || item.isHidden()) {
			return -1;
		}
		final int slotIndex = getFirstEmptySlot();
		if (slotIndex == -1) {
			if (playUserUISounds) {
				simulation.getCommandErrorListener().showInterfaceError(hero.getPlayerIndex(),
						CommandStringErrorKeys.INVENTORY_IS_FULL);
			}
			return -1;
		}
		this.contents[slotIndex] = item;
		item.setHidden(true);
		hero.onPickUpItem(simulation, item, playUserUISounds);
		return slotIndex;
	}

	@Override
	public void setItemAbility(final CItem item, final int slot) {
		// Records the carried Warcraft III item that grants this bag, so the WoW
		// container natives can resolve a bag-bar slot to its CAbilityBag by item.
		this.item = item;
	}

	@Override
	public CItem getItem() {
		return this.item;
	}

	public int getSlotCount() {
		return this.contents.length;
	}

	public CItem getItemInSlot(final int slotIndex) {
		if ((slotIndex < 0) || (slotIndex >= this.contents.length)) {
			return null;
		}
		return this.contents[slotIndex];
	}

	public void setItemInSlot(final int slotIndex, final CItem item) {
		if ((slotIndex >= 0) && (slotIndex < this.contents.length)) {
			this.contents[slotIndex] = item;
		}
	}

	public int getFirstEmptySlot() {
		for (int i = 0; i < this.contents.length; i++) {
			if (this.contents[i] == null) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getSlotOf(final CItem item) {
		for (int i = 0; i < this.contents.length; i++) {
			if (this.contents[i] == item) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void removeItemForMove(final CSimulation game, final CUnit hero, final CItem item) {
		final int slot = getSlotOf(item);
		if (slot != -1) {
			this.contents[slot] = null;
		}
		// Items in a bag grant no abilities and the item stays hidden, so nothing else to undo.
	}

	@Override
	public void placeItemForMove(final CSimulation game, final CUnit hero, final CItem item, final int slotIndex) {
		this.contents[slotIndex] = item;
		item.setHidden(true);
	}

	private boolean isBagDragOrder(final int orderId) {
		return (orderId >= OrderIds.bagitemdrag00) && (orderId < (OrderIds.bagitemdrag00 + this.contents.length));
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int playerIndex, final int orderId,
			final AbilityActivationReceiver receiver) {
		// The bag participates in the Warcraft III order system for picking up items
		// (getitem/smart) and rearranging its own contents (bagitemdrag). Everything
		// else is left to the unit's other abilities.
		if ((orderId == OrderIds.getitem) || (orderId == OrderIds.smart) || isBagDragOrder(orderId)) {
			receiver.useOk();
		}
		else {
			receiver.notAnActiveAbility();
		}
	}

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int playerIndex, final int orderId,
			final boolean autoOrder, final CWidget target, final AbilityTargetCheckReceiver<CWidget> receiver) {
		if (((orderId == OrderIds.getitem) || (orderId == OrderIds.smart)) && !target.isDead()) {
			if (target instanceof CItem) {
				final CItem targetItem = (CItem) target;
				if (targetItem.isHidden()) {
					receiver.orderIdNotAccepted();
					return;
				}
				// Take an item only as OVERFLOW: if the unit's normal inventory still has a
				// free slot, defer to it (the inventory shares these order ids and is checked
				// in the same ability iteration). Items that auto-use on acquisition are the
				// inventory's job too, so leave those alone.
				final boolean autoUseOnAcquire = targetItem.getItemType().isUseAutomaticallyWhenAcquired()
						&& targetItem.getItemType().isActivelyUsed();
				final CAbilityInventory inventory = unit.getInventoryData();
				final boolean inventoryHasRoom = (inventory != null) && (inventory.getFirstEmptySlot() != -1);
				if (!autoUseOnAcquire && !inventoryHasRoom && (getFirstEmptySlot() != -1)) {
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
		else if (isBagDragOrder(orderId)) {
			if ((target instanceof CItem) && (getSlotOf((CItem) target) != -1)) {
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

	@Override
	public void checkCanTarget(final CSimulation game, final CUnit unit, final int playerIndex, final int orderId,
			final boolean autoOrder, final AbilityPointTarget target,
			final AbilityTargetCheckReceiver<AbilityPointTarget> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int playerIndex,
			final int orderId, final boolean autoOrder, final AbilityTargetCheckReceiver<Void> receiver) {
		receiver.orderIdNotAccepted();
	}

	@Override
	public <T> T visit(final CAbilityVisitor<T> visitor) {
		// Intentionally not dispatched to the visitor: the bag must stay invisible to
		// the Warcraft III command card and rawcode/visitor based logic. Callers that
		// need it locate it via instanceof CAbilityBag. Returning null is handled by
		// every visitor consumer (command-card population ignores the result; rawcode
		// lookups skip nulls).
		return null;
	}

	@Override
	public void onAdd(final CSimulation game, final CUnit unit) {
		this.behaviorGetItem = new CBehaviorBagGetItem(unit, this);
	}

	@Override
	public void onRemove(final CSimulation game, final CUnit unit) {
		// The bag ability is removed when its carried item is dropped/lost. Spill the
		// bag's contents onto the ground at the unit so the items aren't deleted with it
		// (mirrors CAbilityInventory.onRemove for the unit's own item slots).
		for (int i = 0; i < this.contents.length; i++) {
			final CItem content = this.contents[i];
			if (content != null) {
				unit.onDropItem(game, content, false);
				this.contents[i] = null;
				content.setHidden(false);
				content.setPointAndCheckUnstuck(unit.getX(), unit.getY(), game);
			}
		}
	}

	@Override
	public void onTick(final CSimulation game, final CUnit unit) {
	}

	@Override
	public boolean checkBeforeQueue(final CSimulation game, final CUnit caster, final int playerIndex,
			final int orderId, final boolean autoOrder, final AbilityTarget target) {
		// Rearranging items within the bag is resolved instantly here (mirrors how
		// CAbilityInventory handles itemdrag), so no behavior needs to begin. The
		// dragged item is the target; the destination slot is encoded in the order id.
		if (isBagDragOrder(orderId)) {
			final int destinationIndex = orderId - OrderIds.bagitemdrag00;
			for (int i = 0; i < this.contents.length; i++) {
				if (this.contents[i] == target) {
					final CItem temp = this.contents[i];
					this.contents[i] = this.contents[destinationIndex];
					this.contents[destinationIndex] = temp;
					return false;
				}
			}
			// The item is not in this bag: it's being dragged in from another bag or the
			// unit inventory, so move it (and swap any occupant back out) across containers.
			if (target instanceof CItem) {
				CItemSlotHolder.transfer(game, caster, (CItem) target, this, destinationIndex);
			}
			return false;
		}
		return super.checkBeforeQueue(game, caster, playerIndex, orderId, autoOrder, target);
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int playerIndex, final int orderId,
			final boolean autoOrder, final CWidget target) {
		// A pickup routed to this bag (because the inventory was full): walk to the item
		// and store it on arrival, exactly like the inventory's get-item behavior.
		if ((orderId == OrderIds.getitem) || (orderId == OrderIds.smart)) {
			final CItem targetItem = target.visit(AbilityTargetVisitor.ITEM);
			if (targetItem != null) {
				return this.behaviorGetItem.reset(game, targetItem);
			}
		}
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int playerIndex, final int orderId,
			final boolean autoOrder, final AbilityPointTarget point) {
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int playerIndex, final int orderId,
			final boolean autoOrder) {
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	public void onCancelFromQueue(final CSimulation game, final CUnit unit, final int playerIndex, final int orderId) {
	}

	@Override
	public void onDeath(final CSimulation game, final CUnit cUnit) {
	}

	@Override
	public boolean isPhysical() {
		return false;
	}

	@Override
	public boolean isMagic() {
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
