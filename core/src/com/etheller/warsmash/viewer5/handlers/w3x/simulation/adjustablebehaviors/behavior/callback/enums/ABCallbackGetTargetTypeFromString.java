package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.enums;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class ABCallbackGetTargetTypeFromString extends ABTargetTypeCallback {

	private ABStringCallback id;

	@Override
	public CTargetType callback(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		return CTargetType.valueOf(this.id.callback(caster, localStore, castId));
	}

}
