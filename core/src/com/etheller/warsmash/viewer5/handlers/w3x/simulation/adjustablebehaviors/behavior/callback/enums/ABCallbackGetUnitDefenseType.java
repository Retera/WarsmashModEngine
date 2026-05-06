package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.unit.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDefenseType;

public class ABCallbackGetUnitDefenseType extends ABDefenseTypeCallback {

	private ABUnitCallback unit;

	@Override
	public CDefenseType callback(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		return unit.callback(caster, localStore, castId).getDefenseType();
	}

}
