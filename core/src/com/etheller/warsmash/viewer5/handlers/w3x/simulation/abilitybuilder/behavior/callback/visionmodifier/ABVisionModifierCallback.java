package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.visionmodifier;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CFogModifier;

public abstract class ABVisionModifierCallback implements ABCallback {

	abstract public CFogModifier callback(final CSimulation game, final CUnit caster, final Map<String, Object> localStore, final int castId);
}
