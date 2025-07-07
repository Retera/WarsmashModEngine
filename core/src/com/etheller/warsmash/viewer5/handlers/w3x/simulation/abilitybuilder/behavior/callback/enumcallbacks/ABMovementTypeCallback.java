package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCallback;

public abstract class ABMovementTypeCallback implements ABCallback {

	abstract public MovementType callback(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId);
}
