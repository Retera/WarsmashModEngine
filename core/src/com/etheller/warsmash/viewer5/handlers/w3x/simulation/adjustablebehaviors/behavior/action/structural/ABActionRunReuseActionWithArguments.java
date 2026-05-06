package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.structural;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.ABAbilityBuilderConfiguration;

public class ABActionRunReuseActionWithArguments implements ABSingleAction {

	private ABStringCallback name;
	private Map<String, ABCallback> arguments;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		final ABAbilityBuilderConfiguration config = (localStore.originAbility).getConfig();
		final String keyS = name.callback(caster, localStore, castId);
		if (config.getReuseActions() != null) {
			List<ABAction> actions = config.getReuseActions().get(keyS);
			if (actions != null && !actions.isEmpty()) {
				if (arguments != null && !arguments.isEmpty()) {
					for (String argKey : arguments.keySet()) {
						localStore.put(ABLocalStoreKeys.combineArgumentKey(argKey), arguments.get(argKey));
					}
				}
				for (final ABAction action : actions) {
					action.runAction(caster, localStore, castId);
				}
			} else {
//				System.err.println("Trying to run ReuseAction with args, but key is missing or empty: " + keyS);
			}
		} else {
//			System.err.println("Trying to run ReuseAction with args (\"+keyS+\"), but none defined");
		}
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "TODOJASS";
	}

}
