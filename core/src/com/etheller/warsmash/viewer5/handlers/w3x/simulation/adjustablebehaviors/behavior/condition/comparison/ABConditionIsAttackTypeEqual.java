package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.comparison;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums.ABAttackTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;

public class ABConditionIsAttackTypeEqual extends ABBooleanCallback {

	private ABAttackTypeCallback attackType1;
	private ABAttackTypeCallback attackType2;

	@Override
	public Boolean callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		CAttackType lA = attackType1.callback(caster, localStore, castId);
		CAttackType rA = attackType2.callback(caster, localStore, castId);
		if (lA == null) {
			if (rA == null) {
				return true;
			}
			return false;
		}
		return lA.equals(rA);
	}

}
