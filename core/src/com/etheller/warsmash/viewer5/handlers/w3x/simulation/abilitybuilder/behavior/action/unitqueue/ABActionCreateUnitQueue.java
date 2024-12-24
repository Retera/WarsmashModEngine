package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unitqueue;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;

public class ABActionCreateUnitQueue implements ABSingleAction {

	private String name;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final Queue<CUnit> queue = new LinkedList<>();
		if (this.name != null) {
			localStore.put("_unitqueue_" + this.name, queue);
		}
		localStore.put(ABLocalStoreKeys.LASTCREATEDUNITQUEUE, queue);
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		if (this.name != null) {
			return "CreateNamedQueueAU(" + jassTextGenerator.getTriggerLocalStore() + ", \"" + this.name + "\")";
		}
		return "CreateQueueAU(" + jassTextGenerator.getTriggerLocalStore() + ")";
	}
}
