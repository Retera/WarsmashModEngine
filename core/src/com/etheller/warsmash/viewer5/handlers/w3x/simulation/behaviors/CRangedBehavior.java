package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;

public interface CRangedBehavior extends CBehavior {
	boolean isWithinRange(final CSimulation simulation);

	void endMove(CSimulation game, boolean interrupted);
}
