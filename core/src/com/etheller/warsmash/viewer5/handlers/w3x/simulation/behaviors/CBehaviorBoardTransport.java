package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.cargohold.CAbilityLoad;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrderTargetWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CBehaviorBoardTransport extends CAbstractRangedBehavior {

	private int higlightOrderId;

	public CBehaviorBoardTransport(final CUnit unit) {
		super(unit);
	}

	public CBehavior reset(final int higlightOrderId, final CWidget target) {
		this.higlightOrderId = higlightOrderId;
		return innerReset(target);
	}

	@Override
	public int getHighlightOrderId() {
		return this.higlightOrderId;
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		CUnit targetUnit = this.target.visit(AbilityTargetVisitor.UNIT);
		if (targetUnit != null) {
			CAbilityLoad loadAbility = CAbilityLoad.getTransportLoad(simulation, this.unit, targetUnit, false, true);
			if (loadAbility != null) {
				return this.unit.canReach(this.target, loadAbility.getCastRange());
			}
		}
		return false;
	}

	@Override
	protected CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {
		final CUnit targetUnit = this.target.visit(AbilityTargetVisitor.UNIT);
		if (targetUnit != null) {
			final CAbilityLoad loadAbility = CAbilityLoad.getTransportLoad(simulation, this.unit, targetUnit, false, false);
			if (loadAbility != null) {
				final COrder currentOrder = targetUnit.getCurrentOrder();
				final boolean queue = currentOrder != null && currentOrder.getOrderId() == OrderIds.smart;
				if (!(currentOrder instanceof COrderTargetWidget && currentOrder.getTarget(simulation) == this.unit)) {
					targetUnit.order(simulation, new COrderTargetWidget(loadAbility.getHandleId(), OrderIds.smart, this.unit.getHandleId(), queue), queue);
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

}
