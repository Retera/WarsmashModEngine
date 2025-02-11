package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.structural;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;

public class ABActionIf implements ABAction {

	private ABCondition condition;
	private List<ABAction> thenActions;
	private List<ABAction> elseActions;

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		if (condition.callback(game, caster, localStore, castId)) {
			for (ABAction periodicAction : thenActions) {
				periodicAction.runAction(game, caster, localStore, castId);
			}
		} else {
			for (ABAction periodicAction : elseActions) {
				periodicAction.runAction(game, caster, localStore, castId);
			}
		}
	}
}
