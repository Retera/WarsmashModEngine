package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;

public interface CRangedBehavior extends CBehavior {
	boolean isWithinRange(final CSimulation simulation);

	void endMove(CSimulation game, boolean interrupted);
	
	AbilityTarget getTarget();
}
