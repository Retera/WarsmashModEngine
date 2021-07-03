package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveVisitor;

public class CBehaviorFollow extends CAbstractRangedBehavior {

	private int higlightOrderId;
	private boolean justAutoAttacked = false;

	public CBehaviorFollow(final CUnit unit) {
		super(unit);
	}

	public CBehavior reset(final int higlightOrderId, final CUnit target) {
		this.higlightOrderId = higlightOrderId;
		return innerReset(target);
	}

	@Override
	public int getHighlightOrderId() {
		return this.higlightOrderId;
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		if(justAutoAttacked = this.unit.autoAcquireAttackTargets(simulation, false)) {
			return true;
		}
		return this.unit.canReach(this.target, this.unit.getAcquisitionRange());
	}

	@Override
	protected CBehavior update(final CSimulation simulation, final boolean withinFacingWindow) {

		this.unit.getUnitAnimationListener().playAnimation(false, PrimaryTag.STAND, SequenceUtils.EMPTY, 1.0f, false);
		return this;
	}

	@Override
	protected CBehavior updateOnInvalidTarget(final CSimulation simulation) {
		unit.setDefaultBehavior(unit.getStopBehavior());
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		if (this.justAutoAttacked) {
			this.justAutoAttacked = false;
			this.unit.getMoveBehavior().reset(target, this, false);
		}
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
