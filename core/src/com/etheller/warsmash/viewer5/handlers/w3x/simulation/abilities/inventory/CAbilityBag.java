package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory;

import java.util.ArrayList;
import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.AbstractGenericAliasedAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.SingleOrderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;
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
 * The contents are held here as the bag's own state store. Like
 * {@link CAbilityInventory}, an item filed into a bag grants its abilities to the
 * carrier (icon hidden, tagged with the item via {@code setItemAbility}) so the
 * item is usable straight out of the bag: a {@code bagitemuse00 + slot} order
 * addressed to this bag forwards to the first ability of the item in that slot
 * (consuming a perishable charge), exactly as {@code itemuseNN} does for the unit
 * inventory. The ability is otherwise invisible to the Warcraft III command card
 * and other ability visitors (see {@link #visit}); all other interaction happens
 * through the WoW container natives that read/write this state.
 */
public class CAbilityBag extends AbstractGenericAliasedAbility implements CItemSlotHolder {

	private final CItem[] contents;
	private final List<CAbility>[] contentsAbilities;
	private CItem item;
	private CBehaviorBagGetItem behaviorGetItem;

	@SuppressWarnings("unchecked")
	public CAbilityBag(final int handleId, final War3ID code, final War3ID alias, final int slotCount) {
		super(handleId, code, alias);
		this.contents = new CItem[Math.max(0, slotCount)];
		this.contentsAbilities = new List[this.contents.length];
		for (int i = 0; i < this.contentsAbilities.length; i++) {
			this.contentsAbilities[i] = new ArrayList<>();
		}
	}

	/**
	 * Stores the item in the first free bag slot, granting the carrier the item's
	 * abilities (mirrors {@link CAbilityInventory#giveItem}), and fires the pickup
	 * event. Returns the slot used or -1 when the bag is full.
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
		storeItem(simulation, hero, item, slotIndex);
		hero.onPickUpItem(simulation, item, playUserUISounds);
		return slotIndex;
	}

	/**
	 * Files the item into the (assumed empty) slot, hides it, and grants the
	 * carrier the item's abilities for that slot.
	 */
	private void storeItem(final CSimulation game, final CUnit hero, final CItem item, final int slotIndex) {
		this.contents[slotIndex] = item;
		item.setHidden(true);
		grantItemAbilities(game, hero, item, slotIndex);
	}

	private void grantItemAbilities(final CSimulation game, final CUnit hero, final CItem item, final int slotIndex) {
		final List<CAbility> slotAbilities = this.contentsAbilities[slotIndex];
		for (final War3ID abilityId : item.getItemType().getAbilityList()) {
			final CAbilityType<?> abilityType = game.getAbilityData().getAbilityType(abilityId);
			if (abilityType != null) {
				final CAbility abilityFromItem = abilityType.createAbility(game.getHandleIdAllocator().createId());
				abilityFromItem.setIconShowing(false);
				abilityFromItem.setItemAbility(item, slotIndex);
				hero.add(game, abilityFromItem);
				slotAbilities.add(abilityFromItem);
			}
		}
	}

	private void revokeItemAbilities(final CSimulation game, final CUnit hero, final int slotIndex) {
		final List<CAbility> slotAbilities = this.contentsAbilities[slotIndex];
		for (final CAbility ability : slotAbilities) {
			hero.remove(game, ability);
		}
		slotAbilities.clear();
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

	@Override
	public int getSlotCount() {
		return this.contents.length;
	}

	@Override
	public CItem getItemInSlot(final int slotIndex) {
		if ((slotIndex < 0) || (slotIndex >= this.contents.length)) {
			return null;
		}
		return this.contents[slotIndex];
	}

	/**
	 * The abilities the item in the given slot currently grants the carrier (empty
	 * when the slot is empty or the item has none). The first one is what a
	 * {@code bagitemuse} order forwards to.
	 */
	@Override
	public List<CAbility> getItemAbilitiesInSlot(final int slotIndex) {
		if ((slotIndex < 0) || (slotIndex >= this.contentsAbilities.length)) {
			return new ArrayList<>();
		}
		return this.contentsAbilities[slotIndex];
	}

	@Override
	public int getUseItemOrderId(final int slotIndex) {
		return OrderIds.bagitemuse00 + slotIndex;
	}

	@Override
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
			revokeItemAbilities(game, hero, slot);
		}
		// The item stays hidden; the caller re-files it into another holder.
	}

	@Override
	public void placeItemForMove(final CSimulation game, final CUnit hero, final CItem item, final int slotIndex) {
		storeItem(game, hero, item, slotIndex);
	}

	/**
	 * Drops the item in the given slot onto the ground at the unit, revoking its
	 * abilities (mirrors {@link CAbilityInventory#dropItem}).
	 */
	public void dropItem(final CSimulation game, final CUnit hero, final int slotIndex, final float x, final float y,
			final boolean playUserUISounds) {
		final CItem droppedItem = this.contents[slotIndex];
		if (droppedItem == null) {
			return;
		}
		hero.onDropItem(game, droppedItem, playUserUISounds);
		this.contents[slotIndex] = null;
		revokeItemAbilities(game, hero, slotIndex);
		droppedItem.setHidden(false);
		droppedItem.setPointAndCheckUnstuck(x, y, game);
	}

	private boolean isBagDragOrder(final int orderId) {
		return (orderId >= OrderIds.bagitemdrag00) && (orderId < (OrderIds.bagitemdrag00 + this.contents.length));
	}

	private boolean isBagUseOrder(final int orderId) {
		return (orderId >= OrderIds.bagitemuse00) && (orderId < (OrderIds.bagitemuse00 + this.contents.length));
	}

	/**
	 * The ability a {@code bagitemuse} order forwards to, or null if the slot is
	 * empty / the item grants none.
	 */
	private CAbility getUseAbility(final int orderId) {
		final List<CAbility> slotAbilities = this.contentsAbilities[orderId - OrderIds.bagitemuse00];
		return slotAbilities.isEmpty() ? null : slotAbilities.get(0);
	}

	private static int forwardedOrderId(final CAbility ability, final int orderId) {
		if (ability instanceof SingleOrderAbility) {
			return ((SingleOrderAbility) ability).getBaseOrderId();
		}
		return orderId;
	}

	private void consumePerishableCharge(final CSimulation game, final CUnit caster, final int slot) {
		final CItem cItem = this.contents[slot];
		if (cItem == null) {
			return;
		}
		final int updatedCharges = cItem.getCharges() - 1;
		if (updatedCharges >= 0) {
			cItem.setCharges(updatedCharges);
			if ((updatedCharges == 0) && cItem.getItemType().isPerishable()) {
				dropItem(game, caster, slot, caster.getX(), caster.getY(), false);
				game.removeItem(cItem);
			}
		}
	}

	@Override
	protected void innerCheckCanUse(final CSimulation game, final CUnit unit, final int playerIndex, final int orderId,
			final AbilityActivationReceiver receiver) {
		// The bag participates in the Warcraft III order system for picking up items
		// (getitem/smart), rearranging its own contents (bagitemdrag) and using the
		// items it holds (bagitemuse). Everything else is left to the unit's other
		// abilities.
		if (isBagUseOrder(orderId)) {
			final CAbility ability = getUseAbility(orderId);
			if (ability != null) {
				ability.checkCanUse(game, unit, playerIndex, forwardedOrderId(ability, orderId), false, receiver);
			}
			else {
				receiver.notAnActiveAbility();
			}
		}
		else if ((orderId == OrderIds.getitem) || (orderId == OrderIds.smart) || isBagDragOrder(orderId)) {
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
		else if (isBagUseOrder(orderId)) {
			final CAbility ability = getUseAbility(orderId);
			if (ability != null) {
				ability.checkCanTarget(game, unit, playerIndex, forwardedOrderId(ability, orderId), false, target,
						receiver);
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
		if (isBagUseOrder(orderId)) {
			final CAbility ability = getUseAbility(orderId);
			if (ability != null) {
				ability.checkCanTarget(game, unit, playerIndex, forwardedOrderId(ability, orderId), false, target,
						receiver);
				return;
			}
		}
		receiver.orderIdNotAccepted();
	}

	@Override
	public void checkCanTargetNoTarget(final CSimulation game, final CUnit unit, final int playerIndex,
			final int orderId, final boolean autoOrder, final AbilityTargetCheckReceiver<Void> receiver) {
		if (isBagUseOrder(orderId)) {
			final CAbility ability = getUseAbility(orderId);
			if (ability != null) {
				ability.checkCanTargetNoTarget(game, unit, playerIndex, forwardedOrderId(ability, orderId), false,
						receiver);
				return;
			}
		}
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
		// (mirrors CAbilityInventory.onRemove for the unit's own item slots); this also
		// revokes the abilities those items were granting.
		for (int i = 0; i < this.contents.length; i++) {
			if (this.contents[i] != null) {
				dropItem(game, unit, i, unit.getX(), unit.getY(), false);
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
					// Swap the two slots, carrying their granted abilities along and re-tagging
					// those abilities with their new slot so item-slot lookups stay correct.
					final CItem temp = this.contents[i];
					final List<CAbility> swapList = this.contentsAbilities[i];
					this.contents[i] = this.contents[destinationIndex];
					this.contentsAbilities[i] = this.contentsAbilities[destinationIndex];
					this.contents[destinationIndex] = temp;
					this.contentsAbilities[destinationIndex] = swapList;
					retagSlotAbilities(i);
					retagSlotAbilities(destinationIndex);
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
		else if (isBagUseOrder(orderId)) {
			final int slot = orderId - OrderIds.bagitemuse00;
			final CAbility ability = getUseAbility(orderId);
			if (ability != null) {
				final boolean checkResult = ability.checkBeforeQueue(game, caster, playerIndex,
						forwardedOrderId(ability, orderId), false, target);
				if (!checkResult) {
					// Instant (no-queue) use: begin() is never called, so the perishable
					// charge is consumed here (same reasoning as CAbilityInventory).
					consumePerishableCharge(game, caster, slot);
				}
				return checkResult;
			}
		}
		return super.checkBeforeQueue(game, caster, playerIndex, orderId, autoOrder, target);
	}

	private void retagSlotAbilities(final int slotIndex) {
		final CItem slotItem = this.contents[slotIndex];
		for (final CAbility ability : this.contentsAbilities[slotIndex]) {
			ability.setItemAbility(slotItem, slotIndex);
		}
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster, final int playerIndex, final int orderId,
			final boolean autoOrder, final CWidget target) {
		if (isBagUseOrder(orderId)) {
			final CAbility ability = getUseAbility(orderId);
			if (ability != null) {
				final CBehavior behavior = ability.begin(game, caster, playerIndex, forwardedOrderId(ability, orderId),
						false, target);
				consumePerishableCharge(game, caster, orderId - OrderIds.bagitemuse00);
				return behavior;
			}
			return caster.pollNextOrderBehavior(game);
		}
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
		if (isBagUseOrder(orderId)) {
			final CAbility ability = getUseAbility(orderId);
			if (ability != null) {
				final CBehavior behavior = ability.begin(game, caster, playerIndex, forwardedOrderId(ability, orderId),
						false, point);
				consumePerishableCharge(game, caster, orderId - OrderIds.bagitemuse00);
				return behavior;
			}
		}
		return caster.pollNextOrderBehavior(game);
	}

	@Override
	public CBehavior beginNoTarget(final CSimulation game, final CUnit caster, final int playerIndex, final int orderId,
			final boolean autoOrder) {
		if (isBagUseOrder(orderId)) {
			final CAbility ability = getUseAbility(orderId);
			if (ability != null) {
				final CBehavior behavior = ability.beginNoTarget(game, caster, playerIndex,
						forwardedOrderId(ability, orderId), false);
				consumePerishableCharge(game, caster, orderId - OrderIds.bagitemuse00);
				return behavior;
			}
		}
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
