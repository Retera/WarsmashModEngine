package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrderNoTarget;

public class COrderStartTransformation extends COrderNoTarget {
	private CBehavior transformBehavior;

	public COrderStartTransformation(final CBehavior transformBehavior, final int orderId) {
		super(0, orderId, false);
		this.transformBehavior = transformBehavior;
	}

	@Override
	public CBehavior begin(final CSimulation game, final CUnit caster) {
		caster.fireOrderEvents(game, this);
		return transformBehavior;
	}

	@Override
	public AbilityTarget getTarget(final CSimulation game) {
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + this.transformBehavior.hashCode();
		result = (prime * result) + this.getOrderId();
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final COrderStartTransformation other = (COrderStartTransformation) obj;
		if (this.transformBehavior.equals(other.transformBehavior)) {
			return false;
		}
		if (this.getOrderId() != other.getOrderId()) {
			return false;
		}
		return true;
	}

	@Override
	public void fireEvents(final CSimulation game, final CUnit unit) {
		unit.fireOrderEvents(game, this);
	}

}

