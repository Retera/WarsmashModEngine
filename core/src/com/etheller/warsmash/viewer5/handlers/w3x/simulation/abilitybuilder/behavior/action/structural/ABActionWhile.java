package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.structural;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionWhile implements ABAction {

	private ABCondition condition;
	private List<ABAction> loopActions;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		while(condition.callback(game, caster, localStore, castId)) {
			for (ABAction periodicAction : loopActions) {
				periodicAction.runAction(game, caster, localStore, castId);
			}
			Boolean brk = (Boolean) localStore.remove(ABLocalStoreKeys.BREAK);
			if (brk != null && brk) {
				break;
			}
		}
	}
}
