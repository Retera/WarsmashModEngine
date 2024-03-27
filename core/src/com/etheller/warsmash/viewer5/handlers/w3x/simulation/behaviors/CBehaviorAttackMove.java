package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;

public class CBehaviorAttackMove implements CRangedBehavior {

	private final CUnit unit;
	private AbilityPointTarget target;
	private boolean justAutoAttacked = false;
	private boolean endedMove = false;

	public CBehaviorAttackMove(final CUnit unit) {
		this.unit = unit;
	}

	public CBehavior reset(final AbilityPointTarget target) {
		this.target = target;
		this.endedMove = false;
		return this;
	}

	@Override
	public int getHighlightOrderId() {
		return OrderIds.attack;
	}

	@Override
	public boolean isWithinRange(final CSimulation simulation) {
		if (this.justAutoAttacked = this.unit.autoAcquireAttackTargets(simulation, false)) {
			// kind of a hack
			return true;
		}
		return innerIsWithinRange(); // TODO this is not how it was meant to be used
	}

	private boolean innerIsWithinRange() {
		return this.unit.distance(this.target.x, this.target.y) <= 16f;
	}

	@Override
	public CBehavior update(final CSimulation simulation) {
		if (this.justAutoAttacked) {
			this.justAutoAttacked = false;
			return this.unit.getCurrentBehavior();
		}
		if (innerIsWithinRange()) {
			this.unit.setDefaultBehavior(this.unit.getStopBehavior());
			return this.unit.pollNextOrderBehavior(simulation);
		}
		return this.unit.getMoveBehavior().reset(this.target, this, false);
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
	public AbilityTarget getTarget() {
		return this.target;
	}

	@Override
	public <T> T visit(final CBehaviorVisitor<T> visitor) {
		return visitor.accept(this);
	}

	@Override
	public CBehaviorCategory getBehaviorCategory() {
		return CBehaviorCategory.MOVEMENT;
	}

}
