package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.listeners;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;

public interface CUnitAbilityEffectReactionListener {
	public boolean onHit(final CSimulation simulation, CUnit source, CUnit target, CAbility ability);
}
