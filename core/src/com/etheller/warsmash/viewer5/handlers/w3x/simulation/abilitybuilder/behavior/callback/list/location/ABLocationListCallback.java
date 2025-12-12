package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.list.location;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.list.ABListCallback;

public abstract class ABLocationListCallback extends ABListCallback<AbilityPointTarget> {

	abstract public List<AbilityPointTarget> callback(final CSimulation game, final CUnit caster,
			final Map<String, Object> localStore, final int castId);
}
