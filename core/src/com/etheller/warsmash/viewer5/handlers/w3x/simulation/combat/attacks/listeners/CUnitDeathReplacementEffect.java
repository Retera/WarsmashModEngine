package com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public interface CUnitDeathReplacementEffect {
	public CUnitDeathReplacementStacking onDeath(final CSimulation simulation, final CUnit unit, final CUnit killer,
			final CUnitDeathReplacementResult result);
}
