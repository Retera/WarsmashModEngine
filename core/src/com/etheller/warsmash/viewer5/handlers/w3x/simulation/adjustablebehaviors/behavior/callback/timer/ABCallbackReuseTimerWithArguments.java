package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.timer;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.ABAbilityBuilderConfiguration;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.timers.CTimer;

public class ABCallbackReuseTimerWithArguments extends ABTimerCallback {

	private ABStringCallback name;
	private Map<String, ABCallback> arguments;

	@Override
	public CTimer callback(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		final ABAbilityBuilderConfiguration config = (localStore.originAbility)
				.getConfig();
		final String keyS = name.callback(caster, localStore, castId);
		if (config.getReuseCallbacks() != null) {
			ABCallback callback = config.getReuseCallbacks().get(keyS);
			if (callback != null && callback instanceof ABTimerCallback) {
				if (arguments != null && !arguments.isEmpty()) {
					for (String argKey : arguments.keySet()) {
						localStore.put(ABLocalStoreKeys.combineArgumentKey(argKey), arguments.get(argKey));
					}
				}
				
				return ((ABTimerCallback) callback).callback(caster, localStore, castId);
			} else {
				System.err.println("Trying to run ReuseTimerCallback, but key is missing or callback was the wrong type: " + keyS);
			}
		} else {
			System.err.println("Trying to run ReuseTimerCallback, but none defined");
		}
		return null;
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "TODOJASS";
	}

}
