package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.structural;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.booleancallbacks.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionRunSubroutine implements ABAction {

	private ABStringCallback key;
	private ABBooleanCallback instanceValue;

	@SuppressWarnings("unchecked")
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		List<ABAction> actions;
		if (instanceValue == null || instanceValue.callback(game, caster, localStore, castId)) {
			actions = (List<ABAction>) localStore.get(ABLocalStoreKeys.combineSubroutineInstanceKey(key.callback(game, caster, localStore, castId), castId));
		} else {
			actions = (List<ABAction>) localStore.get(ABLocalStoreKeys.combineSubroutineKey(key.callback(game, caster, localStore, castId), castId));
		}
		System.err.println("RUNNING SUBROUTINE: " + key.callback(game, caster, localStore, castId));
		if (actions != null) {
			for (ABAction action : actions) {
				action.runAction(game, caster, localStore, castId);
			}
		}
	}
}
