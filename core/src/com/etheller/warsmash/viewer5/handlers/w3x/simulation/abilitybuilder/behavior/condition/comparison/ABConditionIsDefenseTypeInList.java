package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.comparison;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks.ABDefenseTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDefenseType;

public class ABConditionIsDefenseTypeInList extends ABCondition {

	private ABDefenseTypeCallback defenseType;
	private List<ABDefenseTypeCallback> list;

	@Override
	public Boolean callback(CSimulation game, CUnit caster, Map<String, Object> localStore, int castId) {
		CDefenseType theType = defenseType.callback(game, caster, localStore, castId);
		for (ABDefenseTypeCallback lType : list) {
			if (theType == lType.callback(game, caster, localStore, castId)) {
				return true;
			}
		}
		return false;
	}

}
