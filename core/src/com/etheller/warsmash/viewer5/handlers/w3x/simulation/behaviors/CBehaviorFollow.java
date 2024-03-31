package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetStillAliveVisitor;

public class CBehaviorFollow extends CAbstractRangedBehavior {

	private int higlightOrderId;
	private boolean justAutoAttacked = false;

	public CBehaviorFollow(final CUnit unit) {
		super(unit);
	}

	public CBehavior reset(CSimulation game, final int higlightOrderId, final CWidget target) {
		this.higlightOrderId = higlightOrderId;
		return innerReset(game, target);
	}

	@Override
	public int getHighlightOrderId() {
		return this.higlightOrderId;
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		if (this.justAutoAttacked = this.unit.autoAcquireTargets(simulation, false)) {
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
		this.unit.setDefaultBehavior(this.unit.getStopBehavior());
		return this.unit.pollNextOrderBehavior(simulation);
	}

	@Override
	protected boolean checkTargetStillValid(final CSimulation simulation) {
		if (this.justAutoAttacked) {
			this.justAutoAttacked = false;
			this.unit.getMoveBehavior().reset(this.target, this, false);
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

	@Override
	public boolean interruptable() {
		return true;
	}

	@Override
	public CBehaviorCategory getBehaviorCategory() {
		return CBehaviorCategory.MOVEMENT;
	}

}
