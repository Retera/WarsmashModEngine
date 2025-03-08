package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unitqueue;

import java.util.Map;
import java.util.Queue;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitqueue.ABUnitQueueCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABSingleAction;

public class ABActionClearUnitQueue implements ABSingleAction {

	private ABUnitQueueCallback queue;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final Queue<CUnit> groupQueue = this.queue.callback(game, caster, localStore, castId);
		groupQueue.clear();
	}

	@Override
	public String generateJassEquivalent(JassTextGenerator jassTextGenerator) {
		return "GroupClear(" + this.queue.generateJassEquivalent(jassTextGenerator) + ")";
	}
}
