package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;

public interface CBuff extends CAliasedLevelingAbility {
	float getDurationRemaining(CSimulation game);

	float getDurationMax();

	boolean isTimedLifeBar();
}
