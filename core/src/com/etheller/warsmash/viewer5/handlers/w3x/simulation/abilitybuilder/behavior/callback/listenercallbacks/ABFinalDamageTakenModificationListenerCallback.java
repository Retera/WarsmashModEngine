package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.listenercallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.listener.ABFinalDamageTakenModificationListener;

public abstract class ABFinalDamageTakenModificationListenerCallback implements ABCallback {

	abstract public ABFinalDamageTakenModificationListener callback(final CSimulation game, final CUnit caster,
			final Map<String, Object> localStore, final int castId);
}
