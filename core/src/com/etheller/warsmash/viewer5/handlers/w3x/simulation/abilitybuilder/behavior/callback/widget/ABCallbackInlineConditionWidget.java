package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.widget;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;

public class ABCallbackInlineConditionWidget extends ABWidgetCallback {

	private ABCondition condition;
	private ABWidgetCallback pass;
	private ABWidgetCallback fail;
	
	@Override
	public CWidget callback(CSimulation game, CUnit caster, Map<String, Object> localStore, int castId) {
		if (condition != null && condition.callback(game, caster, localStore, castId)) {
			return pass.callback(game, caster, localStore, castId);
		}
		return fail.callback(game, caster, localStore, castId);
	}

}
