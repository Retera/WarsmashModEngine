package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;

/**
 * A slotted holder of {@link CItem}s on a unit: either the unit's own
 * {@link CAbilityInventory} (the WoW "bag bar") or a {@link CAbilityBag} (the
 * backpack / a carried bag). Implementing this lets items be moved between any
 * two of them through one shared {@link #transfer} routine, so the WoW container
 * UI can drag an item from any bag/inventory slot to any other.
 *
 * <p>
 * Every holder grants the carrier the abilities of the items it holds (so they
 * are usable from the unit inventory and from any bag alike); moving an item out
 * of a holder revokes the abilities that holder had granted for it and the
 * destination holder grants them afresh, so an item's ability instances are
 * always owned by exactly one holder slot.
 */
public interface CItemSlotHolder {
	int getSlotCount();

	CItem getItemInSlot(int slotIndex);

	int getSlotOf(CItem item);

	int getFirstEmptySlot();

	/**
	 * The abilities currently granted to the carrier by the item in the given slot
	 * (empty for an empty slot). The first one is the item's "use" ability that the
	 * holder's item-use orders forward to.
	 */
	List<CAbility> getItemAbilitiesInSlot(int slotIndex);

	/**
	 * The Warcraft III order id that uses the item in the given slot of this holder
	 * (itemuseNN for the unit inventory, bagitemuseNN for bags); address the order
	 * to this holder's ability handle id.
	 */
	int getUseItemOrderId(int slotIndex);

	/**
	 * Removes the item from this holder for relocation: clears its slot and removes
	 * the abilities it had granted. The item is left hidden; the caller re-files it
	 * into another holder.
	 */
	void removeItemForMove(CSimulation game, CUnit hero, CItem item);

	/**
	 * Files the item into the given (assumed empty) slot, granting the item's
	 * abilities to the carrier.
	 */
	void placeItemForMove(CSimulation game, CUnit hero, CItem item, int slotIndex);

	/** The holder on the unit that currently contains the item, or null. */
	static CItemSlotHolder findHolderOf(final CUnit unit, final CItem item) {
		for (final CAbility ability : unit.getAbilities()) {
			if (ability instanceof CItemSlotHolder) {
				final CItemSlotHolder holder = (CItemSlotHolder) ability;
				if (holder.getSlotOf(item) != -1) {
					return holder;
				}
			}
		}
		return null;
	}

	/** Whether the item is one that grants the unit a {@link CAbilityBag} (i.e. is itself a bag). */
	static boolean isBagItem(final CUnit unit, final CItem item) {
		for (final CAbility ability : unit.getAbilities()) {
			if ((ability instanceof CAbilityBag) && (((CAbilityBag) ability).getItem() == item)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Moves {@code item} into {@code dest}'s {@code destSlot}, pulling it out of
	 * whichever holder currently has it and swapping any displaced item back into the
	 * vacated source slot. No-op if the item can't be located, the slot is invalid,
	 * or the move would nest a bag inside a bag (which would orphan its contents).
	 */
	static void transfer(final CSimulation game, final CUnit hero, final CItem item, final CItemSlotHolder dest,
			final int destSlot) {
		if ((destSlot < 0) || (destSlot >= dest.getSlotCount())) {
			return;
		}
		final CItemSlotHolder source = findHolderOf(hero, item);
		if ((source == null) || (source == dest)) {
			return;
		}
		final CItem displaced = dest.getItemInSlot(destSlot);
		final int sourceSlot = source.getSlotOf(item);
		// A bag must not be placed inside a bag, or its dormant contents would be orphaned.
		if ((dest instanceof CAbilityBag) && isBagItem(hero, item)) {
			return;
		}
		if ((displaced != null) && (source instanceof CAbilityBag) && isBagItem(hero, displaced)) {
			return;
		}
		source.removeItemForMove(game, hero, item);
		if (displaced != null) {
			dest.removeItemForMove(game, hero, displaced);
			source.placeItemForMove(game, hero, displaced, sourceSlot);
		}
		dest.placeItemForMove(game, hero, item, destSlot);
	}
}
