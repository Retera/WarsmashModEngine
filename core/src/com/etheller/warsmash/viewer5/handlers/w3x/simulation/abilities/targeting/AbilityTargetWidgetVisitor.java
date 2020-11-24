package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;

public class AbilityTargetWidgetVisitor implements AbilityTargetVisitor<CWidget> {
	public static final AbilityTargetWidgetVisitor INSTANCE = new AbilityTargetWidgetVisitor();

	@Override
	public CWidget accept(final AbilityPointTarget target) {
		return null;
	}

	@Override
	public CWidget accept(final CUnit target) {
		return target;
	}

	@Override
	public CWidget accept(final CDestructable target) {
		return target;
	}

	@Override
	public CWidget accept(final CItem target) {
		return target;
	}

}