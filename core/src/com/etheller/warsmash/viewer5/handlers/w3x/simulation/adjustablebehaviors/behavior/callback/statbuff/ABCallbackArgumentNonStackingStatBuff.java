package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.statbuff;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.unit.NonStackingStatBuff;

public class ABCallbackArgumentNonStackingStatBuff extends ABNonStackingStatBuffCallback {

	private ABStringCallback name;

	@Override
	public NonStackingStatBuff callback(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		final String keyS = name.callback(caster, localStore, castId);
		ABCallback cbck = localStore.get(ABLocalStoreKeys.combineArgumentKey(keyS), ABCallback.class);
		if (cbck != null && cbck instanceof ABNonStackingStatBuffCallback) {
			return ((ABNonStackingStatBuffCallback) cbck).callback(caster, localStore, castId);
		} else {
			System.err.println("Trying to run ReuseNonStackingStatBuffCallback, but key is missing or callback was the wrong type: " + keyS);
		}
		return null;
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "TODOJASS";
	}

}
