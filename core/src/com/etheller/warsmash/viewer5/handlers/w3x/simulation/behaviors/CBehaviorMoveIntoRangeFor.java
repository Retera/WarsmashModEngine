package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityRanged;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrderTargetWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CBehaviorMoveIntoRangeFor extends CAbstractRangedBehavior {

	private int higlightOrderId;
	private PairAbilityLocator pairAbilityLocator;

	public CBehaviorMoveIntoRangeFor(final CUnit unit) {
		super(unit);
	}

	public CBehavior reset(final CSimulation game, final int higlightOrderId, final CWidget target,
			final PairAbilityLocator pairAbilityLocator) {
		this.higlightOrderId = higlightOrderId;
		this.pairAbilityLocator = pairAbilityLocator;
		return innerReset(game, target);
	}

	@Override
	public int getHighlightOrderId() {
		return this.higlightOrderId;
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		final CUnit targetUnit = this.target.visit(AbilityTargetVisitor.UNIT);
		if (targetUnit != null) {
			final CAbilityRanged partnerAbility = this.pairAbilityLocator.getPartnerAbility(simulation, this.unit,
					targetUnit, false, true);
			if (partnerAbility != null) {
				return this.unit.canReach(this.target, partnerAbility.getCastRange());
			}
		}
		return false;
	}

	@Override
	protected CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {
		final CUnit targetUnit = this.target.visit(AbilityTargetVisitor.UNIT);
		if (targetUnit != null) {
			final CAbilityRanged partnerAbility = this.pairAbilityLocator.getPartnerAbility(simulation, this.unit,
					targetUnit, false, false);
			if (partnerAbility != null) {
				final COrder currentOrder = targetUnit.getCurrentOrder();
				final boolean queue = (currentOrder != null) && (currentOrder.getOrderId() == OrderIds.smart);
				if (!((currentOrder instanceof COrderTargetWidget)
						&& (currentOrder.getTarget(simulation) == this.unit))) {
					targetUnit.order(simulation, new COrderTargetWidget(partnerAbility.getHandleId(), OrderIds.smart,
							this.unit.getHandleId(), queue), queue);
					return this.unit.pollNextOrderBehavior(simulation);
				} // else we might be looping the queueing and that's wasteful
			}
		}
		this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f, false);
		return this;
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
	public void begin(final CSimulation game) {

	}

	@Override
	public void end(final CSimulation game, final boolean interrupted) {

	}

	@Override
	public void endMove(final CSimulation game, final boolean interrupted) {

	}

	@Override
	public boolean interruptable() {
		return true;
	}

	@Override
	public CBehaviorCategory getBehaviorCategory() {
		return CBehaviorCategory.MOVEMENT;
	}

	public static interface PairAbilityLocator {
		CAbilityRanged getPartnerAbility(final CSimulation game, final CUnit caster, final CUnit transport,
				final boolean ignoreRange, final boolean ignoreDisabled);
	}
}
