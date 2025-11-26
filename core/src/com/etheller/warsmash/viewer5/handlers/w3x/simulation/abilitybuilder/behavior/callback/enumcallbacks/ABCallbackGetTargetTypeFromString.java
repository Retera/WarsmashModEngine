package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.enumcallbacks;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.stringcallbacks.ABStringCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CTargetType;

public class ABCallbackGetTargetTypeFromString extends ABTargetTypeCallback {

	private ABStringCallback id;

	@Override
	public CTargetType callback(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		return CTargetType.valueOf(this.id.callback(game, caster, localStore, castId));
	}

}
