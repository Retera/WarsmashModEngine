package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.destructable;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.destructable.ABDestructableCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;

public class ABActionRemoveDestructable implements ABAction {

	private ABDestructableCallback dest;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		game.removeDestructable(dest.callback(game, caster, localStore, castId));
	}

}
