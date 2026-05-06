package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.comparison;

import java.util.List;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums.ABDefenseTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDefenseType;

public class ABConditionIsDefenseTypeInList extends ABBooleanCallback {

	private ABDefenseTypeCallback defenseType;
	private List<ABDefenseTypeCallback> list;

	@Override
	public Boolean callback(CUnit caster, ABLocalDataStore localStore, int castId) {
		CDefenseType theType = defenseType.callback(caster, localStore, castId);
		for (ABDefenseTypeCallback lType : list) {
			if (theType == lType.callback(caster, localStore, castId)) {
				return true;
			}
		}
		return false;
	}

}
