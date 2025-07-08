package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.condition.unit.behavior;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;

public class ABConditionIsCurrentBehaviorCategoryInList extends ABCondition {

	private ABUnitCallback unit;
	private List<CBehaviorCategory> list;
	
	@Override
	public Boolean callback(CSimulation game, CUnit caster, Map<String, Object> localStore, int castId) {
		CUnit theUnit = caster;
		if (unit != null) {
			theUnit = unit.callback(game, caster, localStore, castId);
		}
		CBehavior beh = theUnit.getCurrentBehavior();
		CBehaviorCategory cat = CBehaviorCategory.IDLE;
		if (beh != null) {
			cat = beh.getBehaviorCategory();
		}
		return list.contains(cat);
	}
}
