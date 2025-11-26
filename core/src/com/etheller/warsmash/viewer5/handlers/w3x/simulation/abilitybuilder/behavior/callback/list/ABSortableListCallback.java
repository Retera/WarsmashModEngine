package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.list;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCallback;

public abstract class ABSortableListCallback<T extends Comparable<? super T>> implements ABCallback {

	abstract public List<T> callback(final CSimulation game, final CUnit caster,
			final Map<String, Object> localStore, final int castId);
}
