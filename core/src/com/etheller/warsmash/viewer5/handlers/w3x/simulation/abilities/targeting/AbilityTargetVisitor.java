package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public interface AbilityTargetVisitor<T> {
	T accept(AbilityPointTarget target);

	T accept(CUnit target);

	T accept(CDestructable target);

	T accept(CItem target);
}
