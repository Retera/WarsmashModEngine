package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.MovementType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;

public class ABCallbackGetMovementTypeFromString extends ABMovementTypeCallback {

	private ABStringCallback id;

	@Override
	public MovementType callback(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		return MovementType.valueOf(this.id.callback(game, caster, localStore, castId));
	}

}
