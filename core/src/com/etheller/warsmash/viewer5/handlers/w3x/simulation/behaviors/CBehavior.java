package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;

public interface CBehavior {
	/**
	 * Executes one step of game simulation of the current order, and then returns
	 * the next behavior for the unit after the result of the update cycle.
	 *
	 * @return
	 */
	CBehavior update(CSimulation game);

	void begin(CSimulation game);

	void end(CSimulation game, boolean interrupted);

	int getHighlightOrderId();
}
