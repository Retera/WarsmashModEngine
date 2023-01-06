package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.structural;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABActionPeriodicExecute implements ABAction {

	private List<ABAction> periodicActions;
	private Float delaySeconds;

	private int nextActiveTick = 0;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore) {
		final int currentTick = game.getGameTurnTick();
		if (currentTick >= this.nextActiveTick) {
			final int delayTicks = (int) (this.delaySeconds / WarsmashConstants.SIMULATION_STEP_TIME);
			this.nextActiveTick = currentTick + delayTicks;
			for (ABAction periodicAction : periodicActions) {
				periodicAction.runAction(game, caster, localStore);
			}
		}
	}
}
