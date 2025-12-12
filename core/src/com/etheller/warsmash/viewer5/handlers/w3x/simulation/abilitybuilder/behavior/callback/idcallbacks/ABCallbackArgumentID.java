package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.idcallbacks;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABCallbackArgumentID extends ABIDCallback {

	private ABStringCallback name;

	@Override
	public War3ID callback(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final String keyS = name.callback(game, caster, localStore, castId);
		ABCallback cbck = (ABCallback) localStore.get(ABLocalStoreKeys.combineArgumentKey(keyS));
		if (cbck != null && cbck instanceof ABIDCallback) {
			return ((ABIDCallback) cbck).callback(game, caster, localStore, castId);
		} else {
			System.err.println("Trying to run ReuseIDCallback, but key is missing or callback was the wrong type: " + keyS);
		}
		return null;
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "TODOJASS";
	}

}
