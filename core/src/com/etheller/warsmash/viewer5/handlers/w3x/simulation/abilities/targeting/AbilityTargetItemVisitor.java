package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public class AbilityTargetItemVisitor implements AbilityTargetVisitor<CItem> {
	public static final AbilityTargetItemVisitor INSTANCE = new AbilityTargetItemVisitor();

	@Override
	public CItem accept(final AbilityPointTarget target) {
		return null;
	}

	@Override
	public CItem accept(final CUnit target) {
		return null;
	}

	@Override
	public CItem accept(final CDestructable target) {
		return null;
	}

	@Override
	public CItem accept(final CItem target) {
		return target;
	}

}