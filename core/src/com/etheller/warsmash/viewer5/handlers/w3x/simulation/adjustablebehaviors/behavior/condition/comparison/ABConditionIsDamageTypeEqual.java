package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.comparison;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums.ABDamageTypeCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.trigger.enumtypes.CDamageType;

public class ABConditionIsDamageTypeEqual extends ABBooleanCallback {

	private ABDamageTypeCallback damageType1;
	private ABDamageTypeCallback damageType2;

	@Override
	public Boolean callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		CDamageType lD = damageType1.callback(caster, localStore, castId);
		CDamageType rD = damageType2.callback(caster, localStore, castId);
		if (lD == null) {
			if (rD == null) {
				return true;
			}
			return false;
		}
		return lD.equals(rD);
	}

}
