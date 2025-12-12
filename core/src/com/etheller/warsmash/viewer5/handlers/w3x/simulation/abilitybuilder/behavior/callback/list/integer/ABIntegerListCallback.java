package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.list.integer;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.list.ABSortableListCallback;

public abstract class ABIntegerListCallback extends ABSortableListCallback<Integer> {

	abstract public List<Integer> callback(final CSimulation game, final CUnit caster,
			final Map<String, Object> localStore, final int castId);
}
