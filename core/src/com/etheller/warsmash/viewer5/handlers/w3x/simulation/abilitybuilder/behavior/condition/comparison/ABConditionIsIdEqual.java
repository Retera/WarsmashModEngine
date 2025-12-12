package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.comparison;

import java.util.Map;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks.ABIDCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;

public class ABConditionIsIdEqual extends ABCondition {

	private ABIDCallback id1;
	private ABIDCallback id2;

	@Override
	public Boolean callback(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		War3ID lA = id1.callback(game, caster, localStore, castId);
		War3ID rA = id2.callback(game, caster, localStore, castId);
		if (lA == null) {
			if (rA == null) {
				return true;
			}
			return false;
		}
		return lA.equals(rA);
	}

}
