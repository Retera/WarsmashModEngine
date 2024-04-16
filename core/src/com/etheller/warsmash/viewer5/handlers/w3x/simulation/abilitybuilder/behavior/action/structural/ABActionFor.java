package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.structural;

import java.util.List;
import java.util.Map;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionFor implements ABAction {

	private ABIntegerCallback times;
	private List<ABAction> actions;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		int max = times.callback(game, caster, localStore, castId);
		for(int i = 0; i < max ; i++) {
			localStore.put(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.ITERATORCOUNT, castId), i);
			for (ABAction iterationAction : actions) {
				iterationAction.runAction(game, caster, localStore, castId);
			}
			Boolean brk = (Boolean) localStore.remove(ABLocalStoreKeys.BREAK);
			if (brk != null && brk) {
				break;
			}
		}
	}
}
