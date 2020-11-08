package com.etheller.warsmash.viewer5.handlers.w3x.simulation.test.behavior;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.test.IBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.test.IState;

public class AttackTarget implements IBehavior {
	private final CUnit attackingUnit;
	private final CWidget targetUnit;

	public AttackTarget(final CUnit attackingUnit, final CWidget targetUnit) {
		this.attackingUnit = attackingUnit;
		this.targetUnit = targetUnit;
	}

	@Override
	public IState resolveNext() {
		return null;
	}

}
