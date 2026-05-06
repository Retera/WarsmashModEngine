package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.integers;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.buff.ABBuffCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetBuffUniqueValueInteger extends ABIntegerCallback {

	private ABBuffCallback buff;
	private ABStringCallback key;

	private ABBooleanCallback allowNull;

	@Override
	public Integer callback(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		final String keyS = key.callback(caster, localStore, castId);
		final CBuff theBuff = buff.callback(caster, localStore, castId);
		Integer theVal = theBuff.getUniqueValue(keyS, Integer.class);
		if (theVal != null || (allowNull != null && allowNull.callback(caster, localStore, castId))) {
			return theVal;
		}
		return 0;
	}

}
