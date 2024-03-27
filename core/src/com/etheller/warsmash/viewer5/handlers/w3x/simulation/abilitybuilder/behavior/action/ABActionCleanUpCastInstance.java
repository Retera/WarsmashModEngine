package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABActionCleanUpCastInstance implements ABAction {

	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId) {
		Set<String> keySet = new HashSet<>(localStore.keySet());
		for (String key : keySet) {
			if (key.contains("#"+castId)) {
				localStore.remove(key);
			}
		}
	}
}
