package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class ABCallbackRawTargetType extends ABTargetTypeCallback {

	private CTargetType value;

	@Override
	public CTargetType callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return value;
	}

}
