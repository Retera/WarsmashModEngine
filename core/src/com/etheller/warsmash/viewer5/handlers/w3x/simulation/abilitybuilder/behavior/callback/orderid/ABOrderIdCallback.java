package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.orderid;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCallback;

public abstract class ABOrderIdCallback implements ABCallback {

	abstract public Integer callback(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId);
}
