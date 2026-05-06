package com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.behavior.action.ability;

import java.util.HashSet;
import java.util.Set;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.core.ABSingleAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalDataStore;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.adjustablebehaviors.datastore.ABLocalStoreKeys;

public class ABActionCleanUpCastInstance implements ABSingleAction {

	@Override
	public void runAction(final CUnit caster, final ABLocalDataStore localStore,
			final int castId) {
		final Set<String> keySet = new HashSet<>(localStore.keySet());
		for (final String key : keySet) {
			if (key.contains(ABLocalStoreKeys.combineKey("#", castId))) {
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
