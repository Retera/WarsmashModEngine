package com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;

public interface CUnitBehaviorChangeListener {
	public void onChange(final CSimulation game, CUnit unit, CBehavior previousBehavior, CBehavior newBehavior, boolean channeling);
}
