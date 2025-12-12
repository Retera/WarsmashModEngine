package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.list;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABCallbackArgumentSortableList<T extends Comparable<? super T>> extends ABSortableListCallback<T> {

	private ABStringCallback name;

	@SuppressWarnings("unchecked")
	@Override
	public List<T> callback(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final String keyS = name.callback(game, caster, localStore, castId);
		ABCallback cbck = (ABCallback) localStore.get(ABLocalStoreKeys.combineArgumentKey(keyS));
		if (cbck != null && cbck instanceof ABListCallback) {
			return ((ABListCallback<T>) cbck).callback(game, caster, localStore, castId);
		} else {
			System.err.println("Trying to run ArgumentLocationCallback, but key is missing or callback was the wrong type: " + keyS);
		}
		return null;
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "TODOJASS";
	}

}
