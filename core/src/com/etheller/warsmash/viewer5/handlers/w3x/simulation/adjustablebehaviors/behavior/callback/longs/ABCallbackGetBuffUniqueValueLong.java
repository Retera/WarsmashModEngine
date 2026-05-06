package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.longs;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.buff.ABBuffCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.condition.ABBooleanCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetBuffUniqueValueLong extends ABLongCallback {

	private ABBuffCallback buff;
	private ABStringCallback key;
	
	private ABBooleanCallback allowNull;

	@Override
	public Long callback(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		final String keyS = key.callback(caster, localStore, castId);
		final CBuff theBuff = buff.callback(caster, localStore, castId);
		Long theVal = theBuff.getUniqueValue(keyS, Long.class);
		if (theVal != null || (allowNull != null && allowNull.callback(caster, localStore, castId))) {
			return theVal;
		}
		return 0l;
	}

}
