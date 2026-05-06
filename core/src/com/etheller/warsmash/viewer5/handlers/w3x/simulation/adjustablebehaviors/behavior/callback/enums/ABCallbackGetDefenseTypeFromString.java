package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDefenseType;

public class ABCallbackGetDefenseTypeFromString extends ABDefenseTypeCallback {

	private ABStringCallback id;

	@Override
	public CDefenseType callback(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		return CDefenseType.valueOf(this.id.callback(caster, localStore, castId));
	}

}
