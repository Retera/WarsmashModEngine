package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.unit.behavior;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;

public class ABConditionIsCurrentBehaviorCategoryInList extends ABBooleanCallback {

	private ABUnitCallback unit;
	private List<CBehaviorCategory> list;
	
	@Override
	public Boolean callback(CUnit caster, ABLocalDataStore localStore, int castId) {
		CUnit theUnit = caster;
		if (unit != null) {
			theUnit = unit.callback(caster, localStore, castId);
		}
		CBehavior beh = theUnit.getCurrentBehavior();
		CBehaviorCategory cat = CBehaviorCategory.IDLE;
		if (beh != null) {
			cat = beh.getBehaviorCategory();
		}
		return list.contains(cat);
	}
}
