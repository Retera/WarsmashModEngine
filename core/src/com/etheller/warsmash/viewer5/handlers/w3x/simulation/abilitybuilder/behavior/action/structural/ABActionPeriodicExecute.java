package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.structural;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionPeriodicExecute implements ABAction {

	private List<ABAction> periodicActions;
	private ABFloatCallback delaySeconds;
	private ABBooleanCallback initialTick;
	
	private ABCallback unique;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		int nextActiveTick = 0;
		Object u = null;
		if (unique != null) {
			u = unique.callback(game, caster, localStore, castId);
			if (localStore.containsKey(ABLocalStoreKeys.PERIODICNEXTTICK+castId+"$"+u)) {
				nextActiveTick = (int) localStore.get(ABLocalStoreKeys.PERIODICNEXTTICK+castId+"$"+u);
			}
		} else {
			if (localStore.containsKey(ABLocalStoreKeys.PERIODICNEXTTICK+castId)) {
				nextActiveTick = (int) localStore.get(ABLocalStoreKeys.PERIODICNEXTTICK+castId);
			}
		}
		
		final int currentTick = game.getGameTurnTick();
		if (currentTick >= nextActiveTick) {
			final int delayTicks = (int) (this.delaySeconds.callback(game, caster, localStore, castId) / WarsmashConstants.SIMULATION_STEP_TIME);
			if (nextActiveTick == 0) {
				nextActiveTick = currentTick + delayTicks;
				if (initialTick != null && initialTick.callback(game, caster, localStore, castId)) {
					for (ABAction periodicAction : periodicActions) {
						periodicAction.runAction(game, caster, localStore, castId);
					}
				}
			} else {
				nextActiveTick = currentTick + delayTicks;
				for (ABAction periodicAction : periodicActions) {
					periodicAction.runAction(game, caster, localStore, castId);
				}
			}
		}
		

		if (unique != null) {
			localStore.put(ABLocalStoreKeys.PERIODICNEXTTICK+castId+"$"+u, nextActiveTick);
		} else {
			localStore.put(ABLocalStoreKeys.PERIODICNEXTTICK+castId, nextActiveTick);
		}
	}
}
