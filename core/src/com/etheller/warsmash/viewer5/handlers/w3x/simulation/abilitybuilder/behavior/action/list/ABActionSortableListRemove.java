package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.list;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.integercallbacks.ABIntegerCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.list.ABSortableListCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABCallback;

public class ABActionSortableListRemove implements ABAction {

	private ABSortableListCallback<?> list;
	private ABIntegerCallback index;
	private ABCallback object;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final List<?> l = this.list.callback(game, caster, localStore, castId);
		if (object != null) {
			l.remove(object.callback(game, caster, localStore, castId));
		} else {
			l.remove(index.callback(game, caster, localStore, castId).intValue());
		}
	}
}
