package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CBehaviorStun implements CBehavior {

	private final CUnit unit;
	private boolean updateAnims;

	public CBehaviorStun(final CUnit unit, final boolean updateAnims) {
		this.unit = unit;
		this.updateAnims = updateAnims;
	}

	@Override
	public int getHighlightOrderId() {
		return OrderIds.stunned;
	}

	@Override
	public CBehavior update(final CSimulation game) {
		return this;
	}

	@Override
	public void begin(final CSimulation game) {
		if (this.updateAnims)
			this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f,
					true);
	}

	@Override
	public void end(final CSimulation game, final boolean interrupted) {

	}

	@Override
	public boolean interruptable() {
		return false;
	}

	@Override
	public <T> T visit(final CBehaviorVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	public CBehaviorCategory getBehaviorCategory() {
		return CBehaviorCategory.IDLE;
	}
}
