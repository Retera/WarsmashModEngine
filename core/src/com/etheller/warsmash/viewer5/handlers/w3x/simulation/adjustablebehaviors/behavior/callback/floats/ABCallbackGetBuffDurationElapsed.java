package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.floats;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.CBuff;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.buff.ABBuffCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;

public class ABCallbackGetBuffDurationElapsed extends ABFloatCallback {

	private ABBuffCallback buff;

	@Override
	public Float callback(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		final CBuff theBuff = buff.callback(caster, localStore, castId);
		return theBuff.getDurationMax() - theBuff.getDurationRemaining(localStore.game, caster);
	}

}
