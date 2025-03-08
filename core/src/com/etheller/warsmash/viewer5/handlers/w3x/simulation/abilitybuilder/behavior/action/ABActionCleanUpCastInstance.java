package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;

public class ABActionCleanUpCastInstance implements ABSingleAction {

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final Set<String> keySet = new HashSet<>(localStore.keySet());
		for (final String key : keySet) {
			if (key.contains("#" + castId)) {
				localStore.remove(key);
			}
		}
	}

	@Override
	public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
		return "LocalStoreCleanUpCastInstance(" + jassTextGenerator.getTriggerLocalStore() + ", "
				+ jassTextGenerator.getCastId() + ")";
	}
}
