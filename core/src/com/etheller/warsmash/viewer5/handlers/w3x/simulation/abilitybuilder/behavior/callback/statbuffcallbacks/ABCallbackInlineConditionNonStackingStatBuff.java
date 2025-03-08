package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.statbuffcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;

public class ABCallbackInlineConditionNonStackingStatBuff extends ABNonStackingStatBuffCallback {

	private ABCondition condition;
	private ABNonStackingStatBuffCallback pass;
	private ABNonStackingStatBuffCallback fail;
	
	@Override
	public NonStackingStatBuff callback(CSimulation game, CUnit caster, Map<String, Object> localStore, int castId) {
		if (condition != null && condition.callback(game, caster, localStore, castId)) {
			return pass.callback(game, caster, localStore, castId);
		}
		return fail.callback(game, caster, localStore, castId);
	}

}
