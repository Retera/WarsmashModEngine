package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.floatcallbacks.ABFloatCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;

public class ABConditionIsUnitInRangeOfUnit implements ABCondition {

	private ABUnitCallback caster;
	private ABUnitCallback target;
	private ABFloatCallback range;

	@Override
	public boolean evaluate(CSimulation game, CUnit casterUnit, Map<String, Object> localStore, final int castId) {
		return caster.callback(game, casterUnit, localStore, castId).canReach(target.callback(game, casterUnit, localStore, castId),
				range.callback(game, casterUnit, localStore, castId));
	}

}
