package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.destructable;

import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.ability.AbilityBuilderAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.parser.AbilityBuilderConfiguration;

public class ABCallbackReuseDestructableWithArguments extends ABDestructableCallback {

	private ABStringCallback name;
	private Map<String, ABCallback> arguments;

	@Override
	public CDestructable callback(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final AbilityBuilderConfiguration config = ((AbilityBuilderAbility) localStore.get(ABLocalStoreKeys.ABILITY))
				.getConfig();
		final String keyS = name.callback(game, caster, localStore, castId);
		if (config.getReuseCallbacks() != null) {
			ABCallback callback = config.getReuseCallbacks().get(keyS);
			if (callback != null && callback instanceof ABDestructableCallback) {
				if (arguments != null && !arguments.isEmpty()) {
					for (String argKey : arguments.keySet()) {
						localStore.put(ABLocalStoreKeys.combineArgumentKey(argKey), arguments.get(argKey));
					}
				}
				
				return ((ABDestructableCallback) callback).callback(game, caster, localStore, castId);
			} else {
				System.err.println("Trying to run ReuseDestructableCallback, but key is missing or callback was the wrong type: " + keyS);
			}
		} else {
			System.err.println("Trying to run ReuseDestructableCallback, but none defined");
		}
		return null;
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "TODOJASS";
	}

}
