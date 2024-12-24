package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unitlisteners.internalConditions;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCondition;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;

public class ABConditionIsNewBehaviorCategoryInList implements ABCondition {

	private List<CBehaviorCategory> list;
	
	@Override
	public boolean evaluate(CSimulation game, CUnit caster, Map<String, Object> localStore, int castId) {
		CBehavior beh = (CBehavior) localStore.get(ABLocalStoreKeys.POSTCHANGEBEHAVIOR+castId);
		CBehaviorCategory cat = CBehaviorCategory.IDLE;
		if (beh != null) {
			cat = beh.getBehaviorCategory();
		}
		return list.contains(cat);
	}

}
