package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.unitlisteners.behavior.internalcondition;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehavior;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors.CBehaviorCategory;

public class ABConditionIsNewBehaviorCategoryInList extends ABBooleanCallback {

	private List<CBehaviorCategory> list;

	@Override
	public Boolean callback(CUnit caster, ABLocalDataStore localStore, int castId) {
		CBehavior beh = localStore.get(ABLocalStoreKeys.combineKey(ABLocalStoreKeys.POSTCHANGEBEHAVIOR, castId),
				CBehavior.class);
		CBehaviorCategory cat = CBehaviorCategory.IDLE;
		if (beh != null) {
			cat = beh.getBehaviorCategory();
		}
		return list.contains(cat);
	}

}
