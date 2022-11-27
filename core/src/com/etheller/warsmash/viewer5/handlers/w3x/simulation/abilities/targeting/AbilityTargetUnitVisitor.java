package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class AbilityTargetUnitVisitor implements AbilityTargetVisitor<CUnit> {
	public static final AbilityTargetUnitVisitor INSTANCE = new AbilityTargetUnitVisitor();

	@Override
	public CUnit accept(final AbilityPointTarget target) {
		return null;
	}

	@Override
	public CUnit accept(final CUnit target) {
		return target;
	}

	@Override
	public CUnit accept(final CDestructable target) {
		return null;
	}

	@Override
	public CUnit accept(final CItem target) {
		return null;
	}

}