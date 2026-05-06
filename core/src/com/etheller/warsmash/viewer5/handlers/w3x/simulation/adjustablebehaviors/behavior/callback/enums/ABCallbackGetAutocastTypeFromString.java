package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.autocast.AutocastType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetAutocastTypeFromString extends ABAutocastTypeCallback {

	private ABStringCallback id;
	
	@Override
	public AutocastType callback(CUnit caster, ABLocalDataStore localStore, final int castId) {
		return AutocastType.valueOf(id.callback(caster, localStore, castId));
	}

}
