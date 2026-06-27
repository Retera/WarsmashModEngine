package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.inventory;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory.CAbilityBag;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetItemVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CAbstractRangedBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

/**
 * The bag equivalent of {@link CBehaviorGetItem}: walks the unit to a target item
 * and stores it in a {@link CAbilityBag} on arrival. Used when the order system
 * routes a pickup (getitem/smart) to a bag because the unit's inventory is full.
 */
public class CBehaviorBagGetItem extends CAbstractRangedBehavior {
	private final CAbilityBag bag;

	public CBehaviorBagGetItem(final CUnit unit, final CAbilityBag bag) {
		super(unit);
		this.bag = bag;
	}

	public CBehavior reset(final CSimulation game, final CItem targetItem) {
		return innerReset(game, targetItem);
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		return this.unit.canReach(this.target, simulation.getGameplayConstants().getPickupItemRange());
	}

	@Override
	public void endMove(final CSimulation game, final boolean interrupted) {
	}

	@Override
	public void begin(final CSimulation game) {
	}

	@Override
	public void end(final CSimulation game, final boolean interrupted) {
	}

	@Override
	public int getHighlightOrderId() {
		return OrderIds.getitem;
	}

	@Override
	protected CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {
		final CItem targetItem = this.target.visit(AbilityTargetItemVisitor.INSTANCE);
		this.bag.giveItem(simulation, this.unit, targetItem, true);
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	protected CBehavior updateOnInvalidTarget(final CSimulation simulation) {
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		return this.target.visit(AbilityTargetStillAliveVisitor.INSTANCE);
	}

	@Override
	protected void resetBeforeMoving(final CSimulation simulation) {

	}

	@Override
	public boolean interruptable() {
		return true;
	}

	@Override
	public CBehaviorCategory getBehaviorCategory() {
		return CBehaviorCategory.MOVEMENT;
	}

}
