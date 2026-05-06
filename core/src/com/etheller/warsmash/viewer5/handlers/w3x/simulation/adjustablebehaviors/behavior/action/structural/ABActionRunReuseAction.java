package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.structural;

import java.util.List;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.callback.strings.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.parser.ABAbilityBuilderConfiguration;

public class ABActionRunReuseAction implements ABSingleAction {

	private ABStringCallback name;

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore, final int castId) {
		final ABAbilityBuilderConfiguration config = (localStore.originAbility).getConfig();
		final String keyS = name.callback(caster, localStore, castId);
		if (config.getReuseActions() != null) {
			List<ABAction> actions = config.getReuseActions().get(keyS);
			if (actions != null && !actions.isEmpty()) {
				for (final ABAction action : actions) {
					action.runAction(caster, localStore, castId);
				}
			} else {
//				System.err.println("Trying to run ReuseAction, but key is missing or empty: " + keyS);
			}
		} else {
//			System.err.println("Trying to run ReuseAction ("+keyS+"), but none defined");
		}
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "TODOJASS";
	}

}
