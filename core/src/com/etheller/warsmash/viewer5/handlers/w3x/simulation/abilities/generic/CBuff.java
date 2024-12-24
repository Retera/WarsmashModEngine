package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;

public interface CBuff extends CAliasedLevelingAbility {
	float getDurationRemaining(CSimulation game, CUnit unit);

	float getDurationMax();

	boolean isTimedLifeBar();
}
