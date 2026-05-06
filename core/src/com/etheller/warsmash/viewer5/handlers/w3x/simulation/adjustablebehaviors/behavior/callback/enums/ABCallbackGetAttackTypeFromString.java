package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;

public class ABCallbackGetAttackTypeFromString extends ABAttackTypeCallback {

	private ABStringCallback id;
	
	@Override
	public CAttackType callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return CAttackType.valueOf(id.callback(caster, localStore, castId));
	}

}
